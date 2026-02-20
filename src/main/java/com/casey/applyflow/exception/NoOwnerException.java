package com.casey.applyflow.exception;

public class NoOwnerException extends RuntimeException {
    public NoOwnerException(String message) {
        super(message);
    }

    public NoOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
