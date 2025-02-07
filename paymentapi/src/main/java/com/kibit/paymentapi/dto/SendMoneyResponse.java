package com.kibit.paymentapi.dto;

public class SendMoneyResponse {

    private final String message;

    public SendMoneyResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
