package com.rev.app.service;

public interface IEmailService {
    void sendOtpEmail(String to, String otp);
    void sendProfileUpdateNotification(String to, String changeDetails);
    void sendTransactionNotification(String to, String messageDetails);
}
