package com.kibit.paymentapi.dto;

import com.kibit.paymentapi.model.Status;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class SendMoneyRequest {

    @NonNull
    private final UUID idempotencyKey;
    @NonNull
    private final Long fromUserId;
    @NonNull
    private final Long toUserId;
    private final int amount;
    @NonNull
    private final String creditCardDetails;

    public SendMoneyRequest(@NonNull UUID idempotencyKey, @NonNull Long fromUserId, @NonNull Long toUserId, int amount, @NonNull String creditCardDetails) {
        this.idempotencyKey = idempotencyKey;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.creditCardDetails = creditCardDetails;
    }

    @NonNull
    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    @NonNull
    public Long getToUserId() {
        return toUserId;
    }

    @NonNull
    public Long getFromUserId() {
        return fromUserId;
    }

    public int getAmount() {
        return amount;
    }

    @NonNull
    public String getCreditCardDetails() {
        return creditCardDetails;
    }
}
