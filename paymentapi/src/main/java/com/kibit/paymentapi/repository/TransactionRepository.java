package com.kibit.paymentapi.repository;

import com.kibit.paymentapi.model.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Transaction> S save(S entity);
}
