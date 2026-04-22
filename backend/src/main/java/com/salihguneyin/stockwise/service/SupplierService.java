package com.salihguneyin.stockwise.service;

import com.salihguneyin.stockwise.dto.SupplierRequest;
import com.salihguneyin.stockwise.dto.SupplierResponse;
import com.salihguneyin.stockwise.entity.Supplier;
import com.salihguneyin.stockwise.exception.NotFoundException;
import com.salihguneyin.stockwise.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierResponse> getAll() {
        return supplierRepository.findAllByOrderByReliabilityScoreDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public Supplier getEntity(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));
    }

    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.name().trim());
        supplier.setContactEmail(request.contactEmail().trim().toLowerCase());
        supplier.setPhone(request.phone().trim());
        supplier.setCountry(request.country().trim());
        supplier.setLeadTimeDays(request.leadTimeDays());
        supplier.setReliabilityScore(request.reliabilityScore());
        supplier.setPreferred(request.preferred());

        return toResponse(supplierRepository.save(supplier));
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getContactEmail(),
                supplier.getPhone(),
                supplier.getCountry(),
                supplier.getLeadTimeDays(),
                supplier.getReliabilityScore(),
                supplier.isPreferred(),
                supplier.getCreatedAt()
        );
    }
}
