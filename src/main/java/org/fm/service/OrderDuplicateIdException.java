package org.fm.service;

public class OrderDuplicateIdException extends Exception {
    public OrderDuplicateIdException(String message) {
        super(message);
    }

    public OrderDuplicateIdException(String message, Throwable cause) { super(message, cause); }
}
