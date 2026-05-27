package com.natwest.tc.exceptions;

public class IntersectionLimitReachedException extends RuntimeException {
    public IntersectionLimitReachedException(String message) {
        super(message);
    }
}
