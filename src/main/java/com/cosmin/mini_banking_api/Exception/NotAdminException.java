package com.cosmin.mini_banking_api.Exception;

public class NotAdminException extends RuntimeException {
    public NotAdminException(String message) {
        super(message);
    }
}
