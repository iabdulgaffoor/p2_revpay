package com.rev.app.dto;

import com.rev.app.entity.Notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private Long targetId;
    private LocalDateTime createdAt;
}
