package com.casey.applyflow.exception;

public class ApplicationAlreadyAddedException extends RuntimeException {
    public ApplicationAlreadyAddedException(String message) {
        super(message);
    }
}
