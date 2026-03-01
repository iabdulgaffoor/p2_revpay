package com.revpay.domain.repository;

import com.revpay.domain.model.Transaction;
import java.util.Optional;
import java.util.List;

public interface ITransactionRepo {
    Transaction save(Transaction transaction);

    Optional<Transaction> findByTransactionId(String transactionId);

    List<Transaction> findBySenderId(Long senderId);

    List<Transaction> findByReceiverId(Long receiverId);
}
