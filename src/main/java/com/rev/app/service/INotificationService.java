package com.rev.app.service;

import com.rev.app.dto.NotificationDTO;
import com.rev.app.entity.Notification;
import java.util.List;

public interface INotificationService {
    NotificationDTO createNotification(Long userId, String message, Notification.NotificationType type);
    List<NotificationDTO> getUserNotifications(Long userId);
    List<NotificationDTO> getUnreadNotifications(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
