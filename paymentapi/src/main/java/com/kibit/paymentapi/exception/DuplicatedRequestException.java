package com.kibit.paymentapi.exception;

public class DuplicatedRequestException extends RuntimeException {
  public DuplicatedRequestException(String message) {
    super(message);
  }
}
