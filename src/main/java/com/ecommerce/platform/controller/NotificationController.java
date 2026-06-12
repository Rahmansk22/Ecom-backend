package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.NotificationResponse;
import com.ecommerce.platform.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userDetails.getUsername()));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID notificationId
    ) {
        notificationService.markAsRead(notificationId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userDetails.getUsername()));
    }
}
