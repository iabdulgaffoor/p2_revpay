package com.rev.app.repository;

import com.rev.app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {

    @Override
    <S extends Transaction> S save(S entity);

    @Override
    Optional<Transaction> findById(Long id);

    @Override
    List<Transaction> findAll();

    @Override
    void deleteById(Long id);

    @Override
    void delete(Transaction entity);

    List<Transaction> findBySenderId(Long senderId);

    List<Transaction> findByRecipientId(Long recipientId);

    List<Transaction> findBySenderIdOrRecipientId(Long senderId, Long recipientId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'COMPLETED' OR t.status = 'SUCCESS'")
    java.math.BigDecimal getTotalTransactionVolume();
}
