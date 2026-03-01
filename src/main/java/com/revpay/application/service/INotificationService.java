package com.revpay.application.service;

import com.revpay.domain.model.User;

public interface INotificationService {
    void sendNotification(User user, String title, String message, String type);
}
