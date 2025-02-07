package com.kibit.paymentapi.exception;

public class TransactionAlreadyTerminatedException extends RuntimeException {
  public TransactionAlreadyTerminatedException(String message) {
    super(message);
  }
}
