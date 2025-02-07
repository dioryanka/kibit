package com.kibit.consumerapp;

import com.kibit.consumerapp.dto.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private final KafkaProducer kafkaProducer;

    @Autowired
    public KafkaConsumer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @KafkaListener(topics="topic-event-1", groupId="my_group_id")
    public void getMessage(KafkaMessage kafkaMessage) {

        System.out.println("Message received");
        System.out.println(kafkaMessage.toString());

        KafkaMessage response = new KafkaMessage(kafkaMessage.getIdempotencyKey(), kafkaMessage.getFromUserId(), kafkaMessage.getToUserId(),
                kafkaMessage.getAmount(), kafkaMessage.getCreditCardDetails(), "SUCCESS");
        kafkaProducer.sendMessage(response);
    }
}
