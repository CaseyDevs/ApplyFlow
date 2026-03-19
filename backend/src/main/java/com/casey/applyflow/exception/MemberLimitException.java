package com.casey.applyflow.exception;

public class MemberLimitException extends RuntimeException {
    public MemberLimitException(String message) {
        super(message);
    }    

    public MemberLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
