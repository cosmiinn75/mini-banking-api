package com.cosmin.mini_banking_api.Dto;

import java.math.BigDecimal;

public record AccountResponse(
        String username,
        Integer accountNumber,
        String name,
        BigDecimal balance
) {
}
