package com.cosmin.mini_banking_api.Dto;

import java.math.BigDecimal;

public interface StatsResponse {
    String getEmail();
    Long getNumberOfAccounts();
    BigDecimal getTotalBalance();
    Long getNumberOfTransactions();
}