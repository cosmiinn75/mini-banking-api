package com.cosmin.mini_banking_api.Dto;

import com.cosmin.mini_banking_api.Enum.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Integer accountNumber,
        String accountName,
        TransactionType transactionType,
        BigDecimal amount,
        LocalDateTime createdAt

) {
}
