package com.cosmin.mini_banking_api.Dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(
        @NotNull
        @DecimalMin("0.01")
        @DecimalMax("999.99")
        BigDecimal amount
) {
}
