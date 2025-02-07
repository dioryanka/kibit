package com.kibit.paymentapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import org.mapstruct.Builder;

import java.util.UUID;

@Entity
public class Transaction {

    @Id
    private UUID idempotencyKey;
    private Long fromUserId;
    private Long toUserId;
    private int amount;
    //obviously in real life it has to be Object to store the apropiate card info
    private String creditCardDetails;
    @Enumerated(EnumType.STRING)
    private Status status;


    private Transaction() {

    }

    public Transaction(UUID idempotencyKey, Long fromUserId, Long toUserId, int amount, String creditCardDetails, Status status) {
        this.idempotencyKey = idempotencyKey;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.creditCardDetails = creditCardDetails;
        this.status = status;
    }

    public UUID getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(UUID idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCreditCardDetails() {
        return creditCardDetails;
    }

    public void setCreditCardDetails(String creditCardDetails) {
        this.creditCardDetails = creditCardDetails;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "idempotencyKey=" + idempotencyKey +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", amount=" + amount +
                ", creditCardDetails='" + creditCardDetails + '\'' +
                '}';
    }
}
