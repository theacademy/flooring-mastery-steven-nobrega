package org.fm.service;

public class OrderDataValidationException extends RuntimeException {
    public OrderDataValidationException(String message) {
        super(message);
    }

    public OrderDataValidationException(String message, Throwable cause) { super(message, cause); }
}
