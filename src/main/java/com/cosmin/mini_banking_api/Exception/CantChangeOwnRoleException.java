package com.cosmin.mini_banking_api.Exception;

public class CantChangeOwnRoleException extends RuntimeException {
    public CantChangeOwnRoleException(String message) {
        super(message);
    }
}
