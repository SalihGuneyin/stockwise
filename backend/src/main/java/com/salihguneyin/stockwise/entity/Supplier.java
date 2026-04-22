package com.salihguneyin.stockwise.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private Integer leadTimeDays;

    @Column(nullable = false)
    private Integer reliabilityScore;

    @Column(nullable = false)
    private boolean preferred;

    @Column(nullable = false)
    private LocalDate createdAt;

    @PrePersist
    void prePersist() {
        createdAt = createdAt == null ? LocalDate.now() : createdAt;
    }
}
