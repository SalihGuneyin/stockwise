package com.salihguneyin.stockwise.service;

import com.salihguneyin.stockwise.dto.DashboardResponse;
import com.salihguneyin.stockwise.dto.PipelineMetricResponse;
import com.salihguneyin.stockwise.dto.SummaryCardResponse;
import com.salihguneyin.stockwise.entity.MovementType;
import com.salihguneyin.stockwise.repository.ProductRepository;
import com.salihguneyin.stockwise.repository.StockMovementRepository;
import com.salihguneyin.stockwise.repository.SupplierRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockMovementRepository stockMovementRepository;
    private final MovementService movementService;

    public DashboardService(
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            StockMovementRepository stockMovementRepository,
            MovementService movementService
    ) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.movementService = movementService;
    }

    public DashboardResponse getDashboard() {
        long lowStockItems = productRepository.findAll().stream()
                .filter(product -> product.getCurrentStock() <= product.getReorderPoint())
                .count();

        List<SummaryCardResponse> summary = List.of(
                new SummaryCardResponse("Products", productRepository.count(), "ink"),
                new SummaryCardResponse("Preferred Suppliers", supplierRepository.countByPreferredTrue(), "mint"),
                new SummaryCardResponse("Low Stock Items", lowStockItems, "gold"),
                new SummaryCardResponse("Movements Logged", stockMovementRepository.count(), "rose")
        );

        List<PipelineMetricResponse> pipeline = Arrays.stream(MovementType.values())
                .map(type -> new PipelineMetricResponse(type.name(), stockMovementRepository.countByMovementType(type)))
                .toList();

        return new DashboardResponse(summary, pipeline, movementService.getRecent());
    }
}
