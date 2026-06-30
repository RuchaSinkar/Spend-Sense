package com.spendsense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategorySummary {
    private String category;
    private BigDecimal spent;
    private BigDecimal limit;
    private BigDecimal percentage;
    private String status;  // OK / WARNING / CRITICAL / EXCEEDED
}