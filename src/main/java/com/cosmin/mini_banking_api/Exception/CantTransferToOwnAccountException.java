package com.cosmin.mini_banking_api.Exception;

public class CantTransferToOwnAccountException extends RuntimeException {
    public CantTransferToOwnAccountException(String message) {
        super(message);
    }
}
