package com.revpay.domain.enums;

public enum NotificationType {

    // Invoice Related
    INVOICE_CREATED,
    INVOICE_SENT,
    INVOICE_PAID,
    INVOICE_OVERDUE,
    INVOICE_CANCELLED,

    // Payment Related
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    PAYMENT_REFUNDED,

    // Account Related
    ACCOUNT_CREATED,
    ACCOUNT_VERIFIED,
    ACCOUNT_SUSPENDED,
    ACCOUNT_REACTIVATED,

    // Security Related
    PASSWORD_RESET,
    TRANSACTION_PIN_CREATED,
    TRANSACTION_PIN_CHANGED,
    LOGIN_ALERT,

    // Business Related
    BUSINESS_PROFILE_CREATED,
    BUSINESS_VERIFIED,
    BUSINESS_REJECTED,

    // System
    SYSTEM_ALERT,
    MAINTENANCE_NOTIFICATION
}