package com.cosmin.mini_banking_api.Dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record WithdrawalRequest(
    @DecimalMin("0.01")
    BigDecimal amount
) {
}
