package com.cosmin.mini_banking_api.Dto;

import java.math.BigDecimal;

public record AccountResponse(
        String username,
        Integer account_number,
        String name,
        BigDecimal balance
) {
}
