package com.learning.backend02;

/**
 * Custom exception thrown when a requested user is not found.
 * The @ResponseStatus annotation is one way to map exceptions to HTTP status,
 * but here we handle it via @ExceptionHandler for more flexibility.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
