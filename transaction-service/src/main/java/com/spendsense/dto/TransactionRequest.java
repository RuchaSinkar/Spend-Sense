package com.spendsense.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransactionRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @NotNull @Positive(message = "amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "upiRef is required")
    private String upiRef;
}