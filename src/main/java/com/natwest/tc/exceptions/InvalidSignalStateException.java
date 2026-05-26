package com.natwest.tc.exceptions;

public class InvalidSignalStateException extends RuntimeException {
    public InvalidSignalStateException(final String message) {
        super(message);
    }
}
