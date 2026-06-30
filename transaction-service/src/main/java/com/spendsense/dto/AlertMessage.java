package com.spendsense.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertMessage {

    private String type;       // WARNING / CRITICAL / EXCEEDED
    private String userId;
    private String category;
    private BigDecimal spent;
    private BigDecimal limit;
    private BigDecimal percentage;
    private String message;    // human readable
}