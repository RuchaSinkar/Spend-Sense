package com.spendsense.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "budgets")
@Data
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String category;      // "Food Delivery", "Transport"

    @Column(nullable = false)
    private BigDecimal limitAmount; // ₹3000

    @Column(nullable = false)
    private String month;          // "2026-06"
}