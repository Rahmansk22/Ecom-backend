package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.NotificationResponse;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationResponse> getUserNotifications(String email);
    void markAsRead(UUID notificationId, String email);
    long getUnreadCount(String email);
}
