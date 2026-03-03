package com.rev.app.exception;

/**
 * Thrown when a user provides an incorrect or missing transaction PIN
 * during a money transfer or other PIN-protected operation.
 */
public class InvalidTransactionPinException extends RuntimeException {

    public InvalidTransactionPinException(String message) {
        super(message);
    }
}
