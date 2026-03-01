package com.revpay.infrastructure.repository;

import com.revpay.domain.model.Transaction;
import com.revpay.domain.repository.ITransactionRepo;
import com.revpay.infrastructure.persistence.IJpaTransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransactionRepoImpl implements ITransactionRepo {
    private final IJpaTransactionRepo jpaTransactionRepo;

    @Override
    public Transaction save(Transaction transaction) {
        return jpaTransactionRepo.save(transaction);
    }

    @Override
    public Optional<Transaction> findByTransactionId(String transactionId) {
        return jpaTransactionRepo.findByTransactionId(transactionId);
    }

    @Override
    public List<Transaction> findBySenderId(Long senderId) {
        return jpaTransactionRepo.findBySenderId(senderId);
    }

    @Override
    public List<Transaction> findByReceiverId(Long receiverId) {
        return jpaTransactionRepo.findByReceiverId(receiverId);
    }
}
