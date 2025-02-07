package com.kibit.consumerapp.dto;

import org.springframework.lang.NonNull;

import java.util.UUID;

public class KafkaMessage {

    private UUID idempotencyKey;
    private Long fromUserId;
    private Long toUserId;
    private int amount;
    private String creditCardDetails;
    private String status;

    public KafkaMessage() {

    }

    public KafkaMessage(@NonNull UUID idempotencyKey,
                        @NonNull Long fromUserId,
                        @NonNull Long toUserId,
                        int amount, @NonNull
                        String creditCardDetails,
                        String status) {
        this.idempotencyKey = idempotencyKey;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.creditCardDetails = creditCardDetails;
        this.status = status;
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

    @NonNull
    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "KafkaMessage{" +
                "idempotencyKey=" + idempotencyKey +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", amount=" + amount +
                ", creditCardDetails='" + creditCardDetails + '\'' +
                ", status=" + status +
                '}';
    }
}
