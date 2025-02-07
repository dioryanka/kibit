package com.kibit.paymentapi.controller;

import com.kibit.paymentapi.dto.SendMoneyResponse;
import com.kibit.paymentapi.exception.DuplicatedRequestException;
import com.kibit.paymentapi.exception.MissingWalletException;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Hidden
@ControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler({DuplicatedRequestException.class})
    public ResponseEntity<SendMoneyResponse> handleExceptions(DuplicatedRequestException ex) {
        return new ResponseEntity<>(new SendMoneyResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingWalletException.class})
    public ResponseEntity<SendMoneyResponse> handleExceptions(MissingWalletException ex) {
        return new ResponseEntity<>(new SendMoneyResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BulkheadFullException.class})
    public ResponseEntity<SendMoneyResponse> handleExceptions(BulkheadFullException ex) {
        return new ResponseEntity<>(new SendMoneyResponse(ex.getMessage()), HttpStatus.TOO_MANY_REQUESTS);
    }
}
