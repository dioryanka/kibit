package com.kibit.consumerapp;

import com.kibit.consumerapp.dto.KafkaMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
public class KafkaProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;

    @Value("${spring.kafka.topic.replymsg}")
    private String topic;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, KafkaMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(@RequestBody KafkaMessage kafkaMessage) {
        System.out.println("Send message to Kafka: " + kafkaMessage.toString());
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), kafkaMessage);
    }
}
