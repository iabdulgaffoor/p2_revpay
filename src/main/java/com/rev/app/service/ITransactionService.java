package com.rev.app.service;

import com.rev.app.dto.TransactionDTO;
import com.rev.app.entity.Transaction.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface ITransactionService {
    TransactionDTO sendMoney(Long senderId, Long recipientId, BigDecimal amount, String note, String pin);
    TransactionDTO getTransactionById(Long transactionId);
    List<TransactionDTO> getTransactionsByUserId(Long userId);
    List<TransactionDTO> filterTransactions(Long userId, TransactionType type, String startDate, String endDate, BigDecimal minAmount, BigDecimal maxAmount, String status);
    List<TransactionDTO> searchTransactions(Long userId, String searchTerm);
    byte[] exportTransactionsToCSV(Long userId);
}
