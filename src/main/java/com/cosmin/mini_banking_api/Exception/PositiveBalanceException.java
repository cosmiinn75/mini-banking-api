package com.cosmin.mini_banking_api.Exception;

public class PositiveBalanceException extends RuntimeException {
    public PositiveBalanceException(String message) {
        super(message);
    }
}
