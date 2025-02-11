package com.kibit.paymentapi.service;

import com.kibit.paymentapi.dto.KafkaMessage;
import com.kibit.paymentapi.dto.SendMoneyRequest;
import com.kibit.paymentapi.dto.SendMoneyResponse;
import com.kibit.paymentapi.exception.DuplicatedRequestException;
import com.kibit.paymentapi.exception.MissingIdempotencyIdException;
import com.kibit.paymentapi.exception.MissingWalletException;
import com.kibit.paymentapi.exception.TransactionAlreadyTerminatedException;
import com.kibit.paymentapi.model.Status;
import com.kibit.paymentapi.model.Transaction;
import com.kibit.paymentapi.model.Wallet;
import com.kibit.paymentapi.repository.TransactionRepository;
import com.kibit.paymentapi.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final TransactionRepository transactionRepository;

    private final WalletRepository walletRepository;

    private final KafkaProducerService producerService;

    @Autowired
    public PaymentService(TransactionRepository transactionRepository,
                          WalletRepository walletRepository,
                          KafkaProducerService producerService) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.producerService = producerService;
    }

    @Transactional
    public SendMoneyResponse proceedMoneyTransfer(SendMoneyRequest sendMoneyRequest) {
        //check the query is duplicated or not
        if (transactionRepository.findById(sendMoneyRequest.idempotencyKey()).isPresent()) {
            LOG.error("The given request has already been available in out transactional db, idempotencyKey: {}", sendMoneyRequest.idempotencyKey());

            throw new DuplicatedRequestException("The current request has been duplicated");
        }

        if (!hasEnoughAmount(sendMoneyRequest.fromUserId(), sendMoneyRequest.amount())) {
            return new SendMoneyResponse("Insufficient funds");
        }

        //Create a transaction Event
        Transaction transaction = createTransactionFromSendMoneyRequest(sendMoneyRequest);

        transactionRepository.save(transaction);

        //Send out notification on Kafka, Kafka producer should call after transaction commit
        // it can be async and prevent the slow kafka
        // with circuit breaker we can ensure the app will not crash in case of Kafka failures
        // Retry mechanism for fault tolerance
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                producerService.sendMessage(convertEntityToKafkaMessage(transaction));
            }
        });

        return new SendMoneyResponse("The sendMoney request for the following key: %s is now EXECUTING".formatted(sendMoneyRequest.idempotencyKey()));
    }

    @Transactional
    public void updateBalanceAndTransactionData(UUID idempotencyId,
                                                Long userId,
                                                Status status) {
        Transaction transaction= transactionRepository.findById(idempotencyId).orElseThrow(
                () -> new MissingIdempotencyIdException("The following idempotencyId has not found in the Transaction table: " + idempotencyId));

        if (transaction.getStatus() == Status.SUCCESS || transaction.getStatus() == Status.FAILED) {
            throw new TransactionAlreadyTerminatedException("The given transaction has already been terminated: " + idempotencyId);
        }

        int chargedAmount = transaction.getAmount();
        transaction.setStatus(status);
        transactionRepository.save(transaction);

        if (status == Status.SUCCESS) {
            Wallet wallet = walletRepository.findById(userId).orElseThrow(
                    () -> new MissingWalletException("Wallet not found for the userId: " + userId));

            int newBalance = wallet.getWalletBalance() - chargedAmount;
            wallet.setWalletBalance(newBalance);

            walletRepository.save(wallet);
        }
    }

    private boolean hasEnoughAmount(Long userId, int chargedAmount) throws MissingWalletException {
        Wallet wallet = walletRepository.findById(userId).orElseThrow(() -> {
            LOG.error("The given userId does not have a Wallet, userId: {}", userId);
            return new MissingWalletException("Wallet not found for the userId");
        });

        return (wallet.getWalletBalance() - chargedAmount) >= 0;
    }

    private Transaction createTransactionFromSendMoneyRequest(SendMoneyRequest sendMoneyRequest) {
        return new Transaction(sendMoneyRequest.idempotencyKey(),
                sendMoneyRequest.fromUserId(),
                sendMoneyRequest.toUserId(),
                sendMoneyRequest.amount(),
                sendMoneyRequest.creditCardDetails(),
                Status.EXECUTING);
    }

    private KafkaMessage convertEntityToKafkaMessage(Transaction transaction) {
        return new KafkaMessage(transaction.getIdempotencyKey(),
                                transaction.getFromUserId(),
                                transaction.getToUserId(),
                                transaction.getAmount(),
                                transaction.getCreditCardDetails(),
                                transaction.getStatus().getValue());
    }
}