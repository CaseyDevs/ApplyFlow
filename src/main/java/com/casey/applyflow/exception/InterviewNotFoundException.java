package com.casey.applyflow.exception;

public class InterviewNotFoundException extends RuntimeException {
    public InterviewNotFoundException(String message) {
        super(message);
    }

    public InterviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
