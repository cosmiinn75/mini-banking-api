package com.cosmin.mini_banking_api.Dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @NotBlank
        String toUsername,

        @Positive
        Integer toAccountNumber,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount
) {
}
