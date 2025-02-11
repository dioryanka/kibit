package com.kibit.paymentapi.repository;

import com.kibit.paymentapi.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findById(Long aLong);
}