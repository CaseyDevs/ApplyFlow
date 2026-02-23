package com.casey.applyflow.exception;

public class ContactNotInCompanyException extends RuntimeException {
    
    public ContactNotInCompanyException(String message) {
        super(message);
    }

    public ContactNotInCompanyException(String message, Throwable cause) {
        super(message, cause);
    }
}
