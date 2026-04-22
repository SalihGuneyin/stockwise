package com.salihguneyin.stockwise.service;

import com.salihguneyin.stockwise.dto.ProductRequest;
import com.salihguneyin.stockwise.dto.ProductResponse;
import com.salihguneyin.stockwise.entity.Product;
import com.salihguneyin.stockwise.exception.NotFoundException;
import com.salihguneyin.stockwise.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new IllegalArgumentException("A product with this SKU already exists");
        }

        Product product = new Product();
        product.setSku(request.sku().trim().toUpperCase());
        product.setName(request.name().trim());
        product.setCategory(request.category().trim());
        product.setWarehouseZone(request.warehouseZone().trim());
        product.setCurrentStock(request.currentStock());
        product.setReorderPoint(request.reorderPoint());
        product.setUnitPrice(request.unitPrice());
        product.setActive(request.active());

        return toResponse(productRepository.save(product));
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory(),
                product.getWarehouseZone(),
                product.getCurrentStock(),
                product.getReorderPoint(),
                product.getUnitPrice(),
                product.isActive(),
                product.getCurrentStock() <= product.getReorderPoint(),
                product.getCreatedAt()
        );
    }
}
