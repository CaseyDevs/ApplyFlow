package com.casey.applyflow.exception;

public class CompanyInUseException extends RuntimeException {
    public CompanyInUseException(String message) {
        super(message);
    }

    public CompanyInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
