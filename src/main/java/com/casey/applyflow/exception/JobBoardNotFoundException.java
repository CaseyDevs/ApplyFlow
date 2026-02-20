package com.casey.applyflow.exception;

public class JobBoardNotFoundException extends RuntimeException {
    public JobBoardNotFoundException(String message) {
        super(message);
    }

    public JobBoardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
