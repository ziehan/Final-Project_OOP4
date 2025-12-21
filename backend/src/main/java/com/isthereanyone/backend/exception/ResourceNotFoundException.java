package com.isthereanyone.backend.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s tidak ditemukan dengan %s: '%s'", resourceName, fieldName, fieldValue));
    }
}

