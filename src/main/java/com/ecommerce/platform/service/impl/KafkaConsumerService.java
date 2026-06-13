package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.NotificationResponse;
import com.ecommerce.platform.model.Notification;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.NotificationRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

// @Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(NotificationRepository notificationRepository,
                                UserRepository userRepository,
                                SimpMessagingTemplate simpMessagingTemplate,
                                ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
    }

    // @KafkaListener(topics = "ecomm-events", groupId = "ecomm-group")
    @Transactional
    public void consume(String message) {
        log.info("Received Event (Manual/Ignored): {}", message);
        try {
            Map<String, String> eventData = objectMapper.readValue(message, Map.class);
            String email = eventData.get("email");
            String title = eventData.get("title");
            String msgText = eventData.get("message");

            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                Notification notification = new Notification(user, title, msgText);
                Notification saved = notificationRepository.save(notification);

                NotificationResponse response = new NotificationResponse(
                        saved.getId(),
                        saved.getTitle(),
                        saved.getMessage(),
                        saved.isRead(),
                        saved.getCreatedAt()
                );
                simpMessagingTemplate.convertAndSendToUser(email, "/queue/notifications", response);
                log.info("Pushed WebSocket notification to user: {}", email);
            }
        } catch (Exception e) {
            log.error("Failed to process consumed Kafka message", e);
        }
    }
}
