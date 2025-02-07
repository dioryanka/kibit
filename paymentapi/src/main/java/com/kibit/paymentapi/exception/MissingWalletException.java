package com.kibit.paymentapi.exception;

public class MissingWalletException extends RuntimeException {
    public MissingWalletException(String message) {
        super(message);
    }
}
