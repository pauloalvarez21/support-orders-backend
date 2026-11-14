package com.gae.supportorders.exception;

public class DuplicateActiveOrderException extends RuntimeException {
    public DuplicateActiveOrderException(String message) {
        super(message);
    }
}
