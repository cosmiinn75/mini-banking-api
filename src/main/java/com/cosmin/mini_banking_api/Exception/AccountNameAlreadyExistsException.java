package com.cosmin.mini_banking_api.Exception;

public class AccountNameAlreadyExistsException extends RuntimeException {
    public AccountNameAlreadyExistsException(String message) {
        super(message);
    }
}
