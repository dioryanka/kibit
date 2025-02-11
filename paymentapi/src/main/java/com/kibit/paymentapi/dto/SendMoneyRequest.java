package com.kibit.paymentapi.dto;

import org.springframework.lang.NonNull;

import java.util.UUID;

public record SendMoneyRequest(@NonNull UUID idempotencyKey, @NonNull Long fromUserId, @NonNull Long toUserId,
                               int amount, @NonNull String creditCardDetails) {

}
