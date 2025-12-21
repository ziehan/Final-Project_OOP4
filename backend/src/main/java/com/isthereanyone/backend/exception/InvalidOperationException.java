package com.isthereanyone.backend.exception;

/**
 * Exception untuk operasi yang tidak valid
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}

