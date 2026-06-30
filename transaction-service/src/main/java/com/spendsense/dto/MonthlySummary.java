package com.spendsense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class MonthlySummary {
    private String userId;
    private String month;
    private BigDecimal totalSpent;
    private List<CategorySummary> categories;
}