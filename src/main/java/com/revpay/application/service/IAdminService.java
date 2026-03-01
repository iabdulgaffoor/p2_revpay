package com.revpay.application.service;

public interface IAdminService {
    void approveBusinessProfile(Long profileId);

    void rejectBusinessProfile(Long profileId);

    void approveLoan(Long loanId);

    void rejectLoan(Long loanId);

    void cancelTransaction(String transactionId);

    void deleteUser(Long userId);

    void resetPassword(Long userId, String newPassword);

    void resetTransactionPin(Long userId, String newPin);

    void toggleUserStatus(Long userId);

    void addMoneyToUser(Long userId, java.math.BigDecimal amount);
}
