package com.kibit.paymentapi.service;

import com.kibit.paymentapi.dto.KafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
public class KafkaProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducerService.class);

    @Value("${spring.kafka.topic.name}")
    private String topic;

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, KafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @CircuitBreaker(maxAttempts = 4, openTimeout = 5000, resetTimeout = 15000)
    public void sendMessage(@RequestBody KafkaMessage kafkaMessage) {
        LOG.info("Send message to Kafka: " + kafkaMessage.toString());
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), kafkaMessage);
    }

    //TODO: fallbackMethod would be needed to store the messages in a queue or db
}
