package com.ecommerce.platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "ecomm-events";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String eventType, String targetUserEmail, String title, String message) {
        log.info("Sending Kafka Event: type={}, user={}", eventType, targetUserEmail);
        String payload = String.format("{\"eventType\":\"%s\",\"email\":\"%s\",\"title\":\"%s\",\"message\":\"%s\"}",
                eventType, targetUserEmail, title.replace("\"", "\\\""), message.replace("\"", "\\\""));
        kafkaTemplate.send(TOPIC, targetUserEmail, payload);
    }
}
