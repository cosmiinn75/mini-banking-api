package com.cosmin.mini_banking_api.Dto;

import java.math.BigDecimal;

public record TransferResponse(
        String fromUsername,
        Integer fromAccountNumber,
        String fromAccountName,
        BigDecimal fromBalanceAfter,

        String toUsername,
        Integer toAccountNumber,
        String toAccountName,
        BigDecimal toBalanceAfter,

        BigDecimal amount,
        String message
) {
}