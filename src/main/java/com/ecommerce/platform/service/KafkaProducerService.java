package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.NotificationResponse;
import com.ecommerce.platform.model.Notification;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.NotificationRepository;
import com.ecommerce.platform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public KafkaProducerService(NotificationRepository notificationRepository,
                                UserRepository userRepository,
                                SimpMessagingTemplate simpMessagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Transactional
    public void sendEvent(String eventType, String targetUserEmail, String title, String message) {
        log.info("Direct Notification Event: type={}, user={}", eventType, targetUserEmail);
        
        User user = userRepository.findByEmail(targetUserEmail).orElse(null);
        if (user != null) {
            Notification notification = new Notification(user, title, message);
            Notification saved = notificationRepository.save(notification);

            NotificationResponse response = new NotificationResponse(
                    saved.getId(),
                    saved.getTitle(),
                    saved.getMessage(),
                    saved.isRead(),
                    saved.getCreatedAt()
            );
            simpMessagingTemplate.convertAndSendToUser(targetUserEmail, "/queue/notifications", response);
            log.info("Pushed WebSocket notification directly to user: {}", targetUserEmail);
        } else {
            log.warn("User not found for email: {}, cannot save notification", targetUserEmail);
        }
    }
}

