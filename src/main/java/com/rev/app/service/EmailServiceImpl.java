package com.rev.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender emailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        log.info("Sending OTP email to {}", to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("revpay.demo@gmail.com");
            message.setTo(to);
            message.setSubject("RevPay - Login OTP");
            message.setText("Your OTP for RevPay login is: " + otp + "\n\nThis OTP is valid for 5 minutes.");
            emailSender.send(message);
            log.info("OTP email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}", to, e);
            // In development, if SMTP is not configured properly, we log it to console
            log.info("DEVELOPMENT MODE OTP for {}: {}", to, otp);
        }
    }

    @Override
    public void sendProfileUpdateNotification(String to, String changeDetails) {
        log.info("Sending profile update notification to {}", to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("revpay.demo@gmail.com");
            message.setTo(to);
            message.setSubject("RevPay - Security Alert: Profile Updated");
            message.setText("Hello,\n\nWe noticed a change in your RevPay account: " + changeDetails + "\n\nIf you did not make this change, please contact support immediately.");
            emailSender.send(message);
            log.info("Profile update email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send profile update email to {}", to, e);
            log.info("DEVELOPMENT MODE Notification for {}: {}", to, changeDetails);
        }
    }

    @Override
    public void sendTransactionNotification(String to, String messageDetails) {
        log.info("Sending transaction notification to {}", to);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("revpay.demo@gmail.com");
            message.setTo(to);
            message.setSubject("RevPay - Transaction Alert");
            message.setText("Hello,\n\nWe wanted to alert you about a recent transaction on your RevPay account:\n\n" + messageDetails + "\n\nIf you did not make this transaction, please contact support immediately.");
            emailSender.send(message);
            log.info("Transaction email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send transaction email to {}", to, e);
            log.info("DEVELOPMENT MODE Transaction Alert for {}: {}", to, messageDetails);
        }
    }
}
