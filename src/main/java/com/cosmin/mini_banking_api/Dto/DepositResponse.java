package com.cosmin.mini_banking_api.Dto;

import java.math.BigDecimal;

public record DepositResponse(
        Integer account_number,
        String account_name,
        BigDecimal balance
) {
}
