import { startTransition, useDeferredValue, useEffect, useState } from 'react'
import './App.css'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

const movementOptions = ['INBOUND', 'OUTBOUND', 'ADJUSTMENT']
const inventoryFilters = ['ALL', 'LOW_STOCK', 'INACTIVE']

const initialProductForm = {
  sku: '',
  name: '',
  category: '',
  warehouseZone: '',
  currentStock: 0,
  reorderPoint: 0,
  unitPrice: 0,
  active: true,
}

const initialSupplierForm = {
  name: '',
  contactEmail: '',
  phone: '',
  country: '',
  leadTimeDays: 7,
  reliabilityScore: 80,
  preferred: false,
}

const initialMovementForm = {
  productId: '',
  supplierId: '',
  movementType: 'INBOUND',
  quantity: 1,
  referenceCode: '',
  movementNotes: '',
}

function App() {
  const [dashboard, setDashboard] = useState({ summary: [], pipeline: [], recentMovements: [] })
  const [products, setProducts] = useState([])
  const [suppliers, setSuppliers] = useState([])
  const [productForm, setProductForm] = useState(initialProductForm)
  const [supplierForm, setSupplierForm] = useState(initialSupplierForm)
  const [movementForm, setMovementForm] = useState(initialMovementForm)
  const [searchTerm, setSearchTerm] = useState('')
  const [inventoryFilter, setInventoryFilter] = useState('ALL')
  const [isLoading, setIsLoading] = useState(true)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [message, setMessage] = useState({ type: 'idle', text: '' })
  const deferredSearch = useDeferredValue(searchTerm)

  const query = deferredSearch.trim().toLowerCase()
  const lowStockCount = products.filter((product) => product.lowStock).length
  const preferredSupplierCount = suppliers.filter((supplier) => supplier.preferred).length

  const filteredProducts = products.filter((product) => {
    const matchesFilter =
      inventoryFilter === 'ALL' ||
      (inventoryFilter === 'LOW_STOCK' && product.lowStock) ||
      (inventoryFilter === 'INACTIVE' && !product.active)

    const matchesSearch =
      query.length === 0 ||
      product.name.toLowerCase().includes(query) ||
      product.sku.toLowerCase().includes(query) ||
      product.category.toLowerCase().includes(query) ||
      product.warehouseZone.toLowerCase().includes(query)

    return matchesFilter && matchesSearch
  })

  useEffect(() => {
    let cancelled = false

    async function bootstrap() {
      try {
        const [dashboardData, productData, supplierData] = await fetchSnapshot()
        if (cancelled) {
          return
        }

        startTransition(() => {
          setDashboard(dashboardData)
          setProducts(productData)
          setSuppliers(supplierData)
        })
      } catch (error) {
        if (!cancelled) {
          setMessage({ type: 'error', text: error.message })
        }
      } finally {
        if (!cancelled) {
          setIsLoading(false)
        }
      }
    }

    bootstrap()

    return () => {
      cancelled = true
    }
  }, [])

  async function handleProductSubmit(event) {
    event.preventDefault()
    setIsSubmitting(true)

    try {
      await apiRequest('/api/products', {
        method: 'POST',
        body: JSON.stringify({
          ...productForm,
          currentStock: Number(productForm.currentStock),
          reorderPoint: Number(productForm.reorderPoint),
          unitPrice: Number(productForm.unitPrice),
        }),
      })

      setProductForm(initialProductForm)
      setMessage({ type: 'success', text: 'Product saved to inventory.' })
      await refreshData({ setDashboard, setProducts, setSuppliers, setIsLoading, setMessage })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleSupplierSubmit(event) {
    event.preventDefault()
    setIsSubmitting(true)

    try {
      await apiRequest('/api/suppliers', {
        method: 'POST',
        body: JSON.stringify({
          ...supplierForm,
          leadTimeDays: Number(supplierForm.leadTimeDays),
          reliabilityScore: Number(supplierForm.reliabilityScore),
        }),
      })

      setSupplierForm(initialSupplierForm)
      setMessage({ type: 'success', text: 'Supplier added successfully.' })
      await refreshData({ setDashboard, setProducts, setSuppliers, setIsLoading, setMessage })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleMovementSubmit(event) {
    event.preventDefault()
    setIsSubmitting(true)

    try {
      await apiRequest('/api/movements', {
        method: 'POST',
        body: JSON.stringify({
          ...movementForm,
          productId: Number(movementForm.productId),
          supplierId: Number(movementForm.supplierId),
          quantity: Number(movementForm.quantity),
        }),
      })

      setMovementForm(initialMovementForm)
      setMessage({ type: 'success', text: 'Stock movement recorded.' })
      await refreshData({ setDashboard, setProducts, setSuppliers, setIsLoading, setMessage })
    } catch (error) {
      setMessage({ type: 'error', text: error.message })
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar panel">
        <div className="topbar-copy">
          <p className="kicker">Stockwise</p>
          <h1>Inventory control dashboard</h1>
          <p className="topbar-text">
            Track warehouse stock, supplier reliability and movement history from a single internal
            operations panel.
          </p>
        </div>
        <div className="topbar-side">
          <article className="status-note">
            <span className="note-label">System</span>
            <strong>{isLoading ? 'Refreshing inventory data' : 'Inventory feed is live'}</strong>
            <p>Local Spring Boot API and React dashboard are connected.</p>
          </article>
          <article className="status-note">
            <span className="note-label">Snapshot</span>
            <strong>
              {products.length} products, {lowStockCount} low stock
            </strong>
            <p>{preferredSupplierCount} suppliers are marked as preferred vendors.</p>
          </article>
        </div>
      </header>

      {message.text ? (
        <div className={`alert alert-${message.type}`}>
          <span>{message.text}</span>
          <button type="button" onClick={() => setMessage({ type: 'idle', text: '' })}>
            Dismiss
          </button>
        </div>
      ) : null}

      <section className="summary-row">
        {dashboard.summary.map((card) => (
          <article key={card.label} className={`summary-card accent-${card.accent}`}>
            <span className="summary-label">{card.label}</span>
            <strong>{card.value}</strong>
          </article>
        ))}
      </section>

      <section className="workspace">
        <main className="workspace-main">
          <section className="panel tracker-panel">
            <div className="panel-header panel-header-wide">
              <div>
                <p className="section-tag">Inventory monitor</p>
                <h2>Product stock board</h2>
                <p className="panel-copy">
                  Review stock levels, reorder points and product availability across warehouse
                  zones.
                </p>
              </div>
              <div className="toolbar">
                <input
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                  placeholder="Search SKU, category or zone"
                />
                <select
                  value={inventoryFilter}
                  onChange={(event) => setInventoryFilter(event.target.value)}
                >
                  {inventoryFilters.map((option) => (
                    <option key={option} value={option}>
                      {formatLabel(option)}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="pipeline-strip">
              {dashboard.pipeline.map((item) => (
                <div key={item.status} className="pipeline-chip">
                  <span>{formatLabel(item.status)}</span>
                  <strong>{item.total}</strong>
                </div>
              ))}
            </div>

            <div className="application-list">
              {filteredProducts.length > 0 ? (
                filteredProducts.map((product) => {
                  const state = getProductState(product)
                  return (
                    <article key={product.id} className="application-card">
                      <div className="application-top">
                        <div>
                          <p className="application-name">{product.name}</p>
                          <p className="application-role">
                            {product.sku} - {product.category} - Zone {product.warehouseZone}
                          </p>
                        </div>
                        <span className={`status-pill ${state.className}`}>{state.label}</span>
                      </div>
                      <div className="application-meta">
                        <span>Stock {product.currentStock}</span>
                        <span>Reorder point {product.reorderPoint}</span>
                        <span>Unit price {formatCurrency(product.unitPrice)}</span>
                      </div>
                      <p className="application-notes">
                        {product.active
                          ? product.lowStock
                            ? 'Stock is below reorder point and should be replenished soon.'
                            : 'Stock is healthy and available for operational use.'
                          : 'This item is marked inactive and not currently being replenished.'}
                      </p>
                      <div className="application-footer">
                        <span className="muted">Created {formatDate(product.createdAt)}</span>
                        <span className="muted">Warehouse zone {product.warehouseZone}</span>
                      </div>
                    </article>
                  )
                })
              ) : (
                <div className="empty-state">
                  <strong>No products match this filter.</strong>
                  <p>Try another filter or clear the search field.</p>
                </div>
              )}
            </div>
          </section>

          <section className="resource-grid">
            <section className="panel compact-panel">
              <div className="panel-header">
                <div>
                  <p className="section-tag">Suppliers</p>
                  <h2>Vendor list</h2>
                </div>
                <span className="muted">{suppliers.length} total</span>
              </div>
              <div className="stack-list">
                {suppliers.map((supplier) => (
                  <article key={supplier.id} className="mini-card">
                    <strong>{supplier.name}</strong>
                    <p>
                      {supplier.country} - Lead time {supplier.leadTimeDays} days
                    </p>
                    <span>
                      Reliability {supplier.reliabilityScore}/100 -{' '}
                      {supplier.preferred ? 'Preferred' : 'Standard'}
                    </span>
                  </article>
                ))}
              </div>
            </section>

            <section className="panel compact-panel">
              <div className="panel-header">
                <div>
                  <p className="section-tag">Recent activity</p>
                  <h2>Movement log</h2>
                </div>
                <span className="muted">{dashboard.recentMovements.length} recent</span>
              </div>
              <div className="stack-list">
                {dashboard.recentMovements.map((movement) => (
                  <article key={movement.id} className="mini-card">
                    <strong>
                      {movement.productName} - {formatLabel(movement.movementType)}
                    </strong>
                    <p>
                      {movement.quantity} units via {movement.supplierName}
                    </p>
                    <span>
                      Resulting stock {movement.resultingStock} - {movement.referenceCode}
                    </span>
                  </article>
                ))}
              </div>
            </section>
          </section>
        </main>

        <aside className="workspace-side">
          <section className="panel form-panel">
            <div className="panel-header">
              <div>
                <p className="section-tag">Catalog</p>
                <h2>Add product</h2>
              </div>
            </div>
            <form className="form-grid" onSubmit={handleProductSubmit}>
              <label>
                SKU
                <input
                  value={productForm.sku}
                  onChange={(event) =>
                    setProductForm((current) => ({ ...current, sku: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Product name
                <input
                  value={productForm.name}
                  onChange={(event) =>
                    setProductForm((current) => ({ ...current, name: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Category
                <input
                  value={productForm.category}
                  onChange={(event) =>
                    setProductForm((current) => ({ ...current, category: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Warehouse zone
                <input
                  value={productForm.warehouseZone}
                  onChange={(event) =>
                    setProductForm((current) => ({
                      ...current,
                      warehouseZone: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label>
                Current stock
                <input
                  type="number"
                  min="0"
                  value={productForm.currentStock}
                  onChange={(event) =>
                    setProductForm((current) => ({
                      ...current,
                      currentStock: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label>
                Reorder point
                <input
                  type="number"
                  min="0"
                  value={productForm.reorderPoint}
                  onChange={(event) =>
                    setProductForm((current) => ({
                      ...current,
                      reorderPoint: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label>
                Unit price
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={productForm.unitPrice}
                  onChange={(event) =>
                    setProductForm((current) => ({
                      ...current,
                      unitPrice: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label className="checkbox-row">
                <input
                  type="checkbox"
                  checked={productForm.active}
                  onChange={(event) =>
                    setProductForm((current) => ({ ...current, active: event.target.checked }))
                  }
                />
                Active item
              </label>
              <button className="primary-button" disabled={isSubmitting} type="submit">
                Save product
              </button>
            </form>
          </section>

          <section className="panel form-panel">
            <div className="panel-header">
              <div>
                <p className="section-tag">Suppliers</p>
                <h2>Add supplier</h2>
              </div>
            </div>
            <form className="form-grid" onSubmit={handleSupplierSubmit}>
              <label>
                Name
                <input
                  value={supplierForm.name}
                  onChange={(event) =>
                    setSupplierForm((current) => ({ ...current, name: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Contact email
                <input
                  type="email"
                  value={supplierForm.contactEmail}
                  onChange={(event) =>
                    setSupplierForm((current) => ({
                      ...current,
                      contactEmail: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label>
                Phone
                <input
                  value={supplierForm.phone}
                  onChange={(event) =>
                    setSupplierForm((current) => ({ ...current, phone: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Country
                <input
                  value={supplierForm.country}
                  onChange={(event) =>
                    setSupplierForm((current) => ({ ...current, country: event.target.value }))
                  }
                  required
                />
              </label>
              <label>
                Lead time
                <input
                  type="number"
                  min="1"
                  max="90"
                  value={supplierForm.leadTimeDays}
                  onChange={(event) =>
                    setSupplierForm((current) => ({
                      ...current,
                      leadTimeDays: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label>
                Reliability score
                <input
                  type="number"
                  min="1"
                  max="100"
                  value={supplierForm.reliabilityScore}
                  onChange={(event) =>
                    setSupplierForm((current) => ({
                      ...current,
                      reliabilityScore: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label className="checkbox-row">
                <input
                  type="checkbox"
                  checked={supplierForm.preferred}
                  onChange={(event) =>
                    setSupplierForm((current) => ({
                      ...current,
                      preferred: event.target.checked,
                    }))
                  }
                />
                Preferred supplier
              </label>
              <button className="primary-button" disabled={isSubmitting} type="submit">
                Save supplier
              </button>
            </form>
          </section>

          <section className="panel form-panel">
            <div className="panel-header">
              <div>
                <p className="section-tag">Movements</p>
                <h2>Record movement</h2>
              </div>
            </div>
            <form className="form-grid" onSubmit={handleMovementSubmit}>
              <label className="wide">
                Product
                <select
                  value={movementForm.productId}
                  onChange={(event) =>
                    setMovementForm((current) => ({ ...current, productId: event.target.value }))
                  }
                  required
                >
                  <option value="">Select product</option>
                  {products.map((product) => (
                    <option key={product.id} value={product.id}>
                      {product.sku} - {product.name}
                    </option>
                  ))}
                </select>
              </label>
              <label className="wide">
                Supplier
                <select
                  value={movementForm.supplierId}
                  onChange={(event) =>
                    setMovementForm((current) => ({ ...current, supplierId: event.target.value }))
                  }
                  required
                >
                  <option value="">Select supplier</option>
                  {suppliers.map((supplier) => (
                    <option key={supplier.id} value={supplier.id}>
                      {supplier.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Type
                <select
                  value={movementForm.movementType}
                  onChange={(event) =>
                    setMovementForm((current) => ({
                      ...current,
                      movementType: event.target.value,
                    }))
                  }
                >
                  {movementOptions.map((option) => (
                    <option key={option} value={option}>
                      {formatLabel(option)}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Quantity
                <input
                  type="number"
                  min="1"
                  value={movementForm.quantity}
                  onChange={(event) =>
                    setMovementForm((current) => ({ ...current, quantity: event.target.value }))
                  }
                  required
                />
              </label>
              <label className="wide">
                Reference code
                <input
                  value={movementForm.referenceCode}
                  onChange={(event) =>
                    setMovementForm((current) => ({
                      ...current,
                      referenceCode: event.target.value,
                    }))
                  }
                  required
                />
              </label>
              <label className="wide">
                Notes
                <textarea
                  rows="4"
                  value={movementForm.movementNotes}
                  onChange={(event) =>
                    setMovementForm((current) => ({
                      ...current,
                      movementNotes: event.target.value,
                    }))
                  }
                  placeholder="Restock for branch rollout, warehouse count correction, outbound kit delivery..."
                  required
                />
              </label>
              <button className="primary-button" disabled={isSubmitting} type="submit">
                Save movement
              </button>
            </form>
          </section>
        </aside>
      </section>
    </div>
  )
}

async function apiRequest(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  })

  if (!response.ok) {
    const errorPayload = await response.json().catch(() => ({}))
    const message =
      errorPayload.message ||
      (errorPayload.validationErrors
        ? Object.values(errorPayload.validationErrors).join(', ')
        : 'Request failed')
    throw new Error(message)
  }

  if (response.status === 204) {
    return null
  }

  return response.json()
}

async function fetchSnapshot() {
  return Promise.all([
    apiRequest('/api/dashboard'),
    apiRequest('/api/products'),
    apiRequest('/api/suppliers'),
  ])
}

async function refreshData({ setDashboard, setProducts, setSuppliers, setIsLoading, setMessage }) {
  setIsLoading(true)

  try {
    const [dashboardData, productData, supplierData] = await fetchSnapshot()
    startTransition(() => {
      setDashboard(dashboardData)
      setProducts(productData)
      setSuppliers(supplierData)
    })
  } catch (error) {
    setMessage({ type: 'error', text: error.message })
  } finally {
    setIsLoading(false)
  }
}

function getProductState(product) {
  if (!product.active) {
    return { label: 'Inactive', className: 'status-new' }
  }

  if (product.lowStock) {
    return { label: 'Low stock', className: 'status-rejected' }
  }

  return { label: 'Healthy', className: 'status-hired' }
}

function formatLabel(value) {
  return value
    .toLowerCase()
    .split('_')
    .map((word) => word[0].toUpperCase() + word.slice(1))
    .join(' ')
}

function formatDate(value) {
  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(value))
}

function formatCurrency(value) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(value)
}

export default App
