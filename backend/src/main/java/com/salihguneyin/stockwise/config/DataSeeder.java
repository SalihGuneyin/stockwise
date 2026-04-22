package com.salihguneyin.stockwise.config;

import com.salihguneyin.stockwise.entity.MovementType;
import com.salihguneyin.stockwise.entity.Product;
import com.salihguneyin.stockwise.entity.StockMovement;
import com.salihguneyin.stockwise.entity.Supplier;
import com.salihguneyin.stockwise.repository.ProductRepository;
import com.salihguneyin.stockwise.repository.StockMovementRepository;
import com.salihguneyin.stockwise.repository.SupplierRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            StockMovementRepository stockMovementRepository
    ) {
        return args -> {
            if (productRepository.count() > 0) {
                return;
            }

            Supplier delta = supplier("Delta Industrial", "ops@delta.example", "+90 212 222 11 11", "Turkey", 7, 91, true);
            Supplier nord = supplier("Nord Components", "sales@nord.example", "+49 30 111 22 33", "Germany", 14, 88, true);
            Supplier atlas = supplier("Atlas Trade", "hello@atlas.example", "+90 216 333 44 55", "Turkey", 5, 76, false);
            supplierRepository.save(delta);
            supplierRepository.save(nord);
            supplierRepository.save(atlas);

            Product barcode = product("HW-001", "Barcode Scanner", "Hardware", "A1", 14, 10, "1499.00", true);
            Product receiptPrinter = product("HW-019", "Receipt Printer", "Hardware", "B2", 6, 8, "3199.00", true);
            Product labelRoll = product("SM-007", "Thermal Label Roll", "Supplies", "C4", 120, 40, "89.90", true);
            Product dock = product("AC-014", "USB-C Dock", "Accessories", "D1", 3, 6, "1799.00", true);
            productRepository.save(barcode);
            productRepository.save(receiptPrinter);
            productRepository.save(labelRoll);
            productRepository.save(dock);

            stockMovementRepository.save(movement(barcode, delta, MovementType.INBOUND, 10, 14, "PO-4201", "Received replenishment for field sales kits.", LocalDate.now().minusDays(8)));
            stockMovementRepository.save(movement(receiptPrinter, nord, MovementType.OUTBOUND, 2, 6, "SO-1021", "Allocated to new branch opening.", LocalDate.now().minusDays(5)));
            stockMovementRepository.save(movement(labelRoll, atlas, MovementType.INBOUND, 60, 120, "PO-4217", "Restocked consumables before quarter close.", LocalDate.now().minusDays(3)));
            stockMovementRepository.save(movement(dock, delta, MovementType.ADJUSTMENT, 1, 3, "ADJ-009", "Cycle count correction after warehouse audit.", LocalDate.now().minusDays(1)));
        };
    }

    private Supplier supplier(
            String name,
            String contactEmail,
            String phone,
            String country,
            int leadTimeDays,
            int reliabilityScore,
            boolean preferred
    ) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactEmail(contactEmail);
        supplier.setPhone(phone);
        supplier.setCountry(country);
        supplier.setLeadTimeDays(leadTimeDays);
        supplier.setReliabilityScore(reliabilityScore);
        supplier.setPreferred(preferred);
        return supplier;
    }

    private Product product(
            String sku,
            String name,
            String category,
            String warehouseZone,
            int currentStock,
            int reorderPoint,
            String unitPrice,
            boolean active
    ) {
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setCategory(category);
        product.setWarehouseZone(warehouseZone);
        product.setCurrentStock(currentStock);
        product.setReorderPoint(reorderPoint);
        product.setUnitPrice(new BigDecimal(unitPrice));
        product.setActive(active);
        return product;
    }

    private StockMovement movement(
            Product product,
            Supplier supplier,
            MovementType movementType,
            int quantity,
            int resultingStock,
            String referenceCode,
            String notes,
            LocalDate movedAt
    ) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setSupplier(supplier);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setResultingStock(resultingStock);
        movement.setReferenceCode(referenceCode);
        movement.setMovementNotes(notes);
        movement.setMovedAt(movedAt);
        return movement;
    }
}
