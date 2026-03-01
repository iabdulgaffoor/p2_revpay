package com.revpay.application.service;

import com.revpay.domain.model.Notification;
import com.revpay.domain.model.User;
import com.revpay.domain.repository.INotificationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final INotificationRepo notificationRepo;

    @Override
    @Transactional
    public void sendNotification(User user, String title, String message, String type) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .build();

        notificationRepo.save(notification);
        System.out.println("Notification sent to " + user.getEmail() + ": " + notification.getTitle());
    }
}
