package com.kibit.paymentapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Wallet {
//due to the time limitation the wallet id represents the userId as well
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int walletBalance;

    public Wallet() {
    }

    public Wallet(int walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(int walletBalance) {
        this.walletBalance = walletBalance;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "id=" + id +
                ", walletBalance=" + walletBalance +
                '}';
    }
}
