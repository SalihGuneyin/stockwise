package com.salihguneyin.stockwise.service;

import com.salihguneyin.stockwise.dto.StockMovementRequest;
import com.salihguneyin.stockwise.dto.StockMovementResponse;
import com.salihguneyin.stockwise.entity.MovementType;
import com.salihguneyin.stockwise.entity.Product;
import com.salihguneyin.stockwise.entity.StockMovement;
import com.salihguneyin.stockwise.repository.ProductRepository;
import com.salihguneyin.stockwise.repository.StockMovementRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final SupplierService supplierService;

    public MovementService(
            StockMovementRepository stockMovementRepository,
            ProductRepository productRepository,
            ProductService productService,
            SupplierService supplierService
    ) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.productService = productService;
        this.supplierService = supplierService;
    }

    public List<StockMovementResponse> getAll() {
        return stockMovementRepository.findAllByOrderByUpdatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StockMovementResponse> getRecent() {
        return stockMovementRepository.findTop6ByOrderByUpdatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public StockMovementResponse create(StockMovementRequest request) {
        Product product = productService.getEntity(request.productId());
        int resultingStock = calculateResultingStock(product, request.movementType(), request.quantity());
        product.setCurrentStock(resultingStock);
        productRepository.save(product);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setSupplier(supplierService.getEntity(request.supplierId()));
        movement.setMovementType(request.movementType());
        movement.setQuantity(request.quantity());
        movement.setResultingStock(resultingStock);
        movement.setReferenceCode(request.referenceCode().trim().toUpperCase());
        movement.setMovementNotes(request.movementNotes().trim());

        return toResponse(stockMovementRepository.save(movement));
    }

    private int calculateResultingStock(Product product, MovementType movementType, int quantity) {
        return switch (movementType) {
            case INBOUND, ADJUSTMENT -> product.getCurrentStock() + quantity;
            case OUTBOUND -> {
                if (product.getCurrentStock() < quantity) {
                    throw new IllegalArgumentException("Outbound quantity cannot be greater than current stock");
                }
                yield product.getCurrentStock() - quantity;
            }
        };
    }

    private StockMovementResponse toResponse(StockMovement movement) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getName(),
                movement.getProduct().getSku(),
                movement.getSupplier().getId(),
                movement.getSupplier().getName(),
                movement.getMovementType(),
                movement.getQuantity(),
                movement.getResultingStock(),
                movement.getReferenceCode(),
                movement.getMovementNotes(),
                movement.getMovedAt(),
                movement.getUpdatedAt()
        );
    }
}
