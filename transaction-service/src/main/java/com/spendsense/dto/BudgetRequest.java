package com.spendsense.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetRequest {
    private String userId;
    private String category;
    private BigDecimal limitAmount;
    private String month;          // format: "2026-06"
}