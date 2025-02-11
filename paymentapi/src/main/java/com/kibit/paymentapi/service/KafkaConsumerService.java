package com.kibit.paymentapi.service;

import com.kibit.paymentapi.dto.KafkaMessage;
import com.kibit.paymentapi.exception.StatusBindingException;
import com.kibit.paymentapi.model.Status;
import com.kibit.paymentapi.repository.TransactionRepository;
import com.kibit.paymentapi.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final PaymentService paymentService;

    @Autowired
    public KafkaConsumerService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics="topic-event-2", groupId="my_group_id")
    public void getMessage(KafkaMessage kafkaMessage){

        LOG.info("Message received: {}", kafkaMessage.toString());

        Status status = Status.fromValue(kafkaMessage.getStatus());

        if (status == null) {
            throw new StatusBindingException("Could not bind the status");
        }

        paymentService.updateBalanceAndTransactionData(kafkaMessage.getIdempotencyKey(), kafkaMessage.getFromUserId(), status);
    }
}
