package com.kibit.paymentapi.exception;

public class MissingIdempotencyIdException extends RuntimeException {
    public MissingIdempotencyIdException(String message) {
        super(message);
    }
}
