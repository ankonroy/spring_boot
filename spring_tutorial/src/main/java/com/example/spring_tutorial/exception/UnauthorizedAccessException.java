package com.example.spring_tutorial.exception;

/**
 * Exception thrown when a user attempts to access a resource they are not authorized to access.
 */
public class UnauthorizedAccessException extends RuntimeException {
    
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

