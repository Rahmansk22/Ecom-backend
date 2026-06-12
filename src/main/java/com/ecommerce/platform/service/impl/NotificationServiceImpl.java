package com.ecommerce.platform.service.impl;

import com.ecommerce.platform.dto.NotificationResponse;
import com.ecommerce.platform.exception.BadRequestException;
import com.ecommerce.platform.exception.ResourceNotFoundException;
import com.ecommerce.platform.model.Notification;
import com.ecommerce.platform.repository.NotificationRepository;
import com.ecommerce.platform.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String email) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getMessage(),
                        n.isRead(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (notification.getUser() == null || !notification.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Notification does not belong to user");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        return notificationRepository.countByUserEmailAndIsReadFalse(email);
    }
}
