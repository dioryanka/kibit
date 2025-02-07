package com.kibit.paymentapi.controller;

import com.kibit.paymentapi.dto.SendMoneyRequest;
import com.kibit.paymentapi.dto.SendMoneyResponse;
import com.kibit.paymentapi.exception.MissingIdempotencyIdException;
import com.kibit.paymentapi.exception.MissingWalletException;
import com.kibit.paymentapi.model.Transaction;
import com.kibit.paymentapi.model.Wallet;
import com.kibit.paymentapi.repository.TransactionRepository;
import com.kibit.paymentapi.repository.WalletRepository;
import com.kibit.paymentapi.service.PaymentService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    private final WalletRepository walletRepository;

    private final TransactionRepository transactionRepository;

    @Autowired
    public PaymentController(PaymentService paymentService,
                             WalletRepository walletRepository,
                             TransactionRepository transactionRepository) {
        this.paymentService = paymentService;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Bulkhead(name = "sendMoneyBulkHead", fallbackMethod = "fallbackSendMoney")
    @PostMapping("/sendMoney")
    public ResponseEntity<SendMoneyResponse> sendMoney(@RequestBody SendMoneyRequest sendMoneyRequest) {
        SendMoneyResponse sendMoneyResponse = paymentService.proceedMoneyTransfer(sendMoneyRequest);

        return new ResponseEntity<>(sendMoneyResponse, HttpStatus.OK);
    }

    @Bulkhead(name = "sendMoneyBulkHead")
    @GetMapping("/getStatus/{idempotencyKey}")
    public ResponseEntity<SendMoneyResponse> getStatus(@PathVariable UUID idempotencyKey) {
        Transaction transaction = transactionRepository.findById(idempotencyKey).orElseThrow(
                () -> new MissingIdempotencyIdException("The following idempotencyId has not found in the Transaction table: " + idempotencyKey));

        SendMoneyResponse sendMoneyResponse = new SendMoneyResponse(transaction.getStatus().getValue());

        return new ResponseEntity<>(sendMoneyResponse, HttpStatus.OK);
    }

    @Bulkhead(name = "sendMoneyBulkHead")
    @GetMapping("/getBalance/{userId}")
    public ResponseEntity<SendMoneyResponse> getBalance(@PathVariable Long userId) {
        Wallet wallet = walletRepository.findById(userId).orElseThrow(
                () -> new MissingWalletException("The following userId does have a wallet: " + userId));

        SendMoneyResponse sendMoneyResponse = new SendMoneyResponse("Balance: " + wallet.getWalletBalance());

        return new ResponseEntity<>(sendMoneyResponse, HttpStatus.OK);
    }
}
