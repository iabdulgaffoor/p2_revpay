package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.Transaction;
import com.rev.app.entity.Transaction.TransactionStatus;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class ITransactionRepositoryTest {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findBySenderIdOrRecipientId_ReturnsTransactions() {
        User sender = createTestUser("sender@t.com");
        User recipient = createTestUser("recipient@t.com");

        Transaction tx = new Transaction();
        tx.setSender(sender);
        tx.setRecipient(recipient);
        tx.setAmount(new BigDecimal("50.00"));
        tx.setStatus(TransactionStatus.COMPLETED);
        entityManager.persistAndFlush(tx);

        List<Transaction> found = transactionRepository.findBySenderIdOrRecipientId(sender.getId(), sender.getId());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }

    @Test
    void getTotalTransactionVolume_ReturnsSum() {
        User user = createTestUser("user@t.com");

        Transaction tx1 = new Transaction();
        tx1.setSender(user);
        tx1.setRecipient(user);
        tx1.setAmount(new BigDecimal("100.00"));
        tx1.setStatus(TransactionStatus.COMPLETED);
        entityManager.persistAndFlush(tx1);

        Transaction tx2 = new Transaction();
        tx2.setSender(user);
        tx2.setRecipient(user);
        tx2.setAmount(new BigDecimal("200.00"));
        tx2.setStatus(TransactionStatus.COMPLETED);
        entityManager.persistAndFlush(tx2);

        BigDecimal total = transactionRepository.getTotalTransactionVolume();

        assertEquals(0, total.compareTo(new BigDecimal("300.00")));
    }

    private User createTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setFullName("Test User");
        user.setPassword("pass");
        user.setRole(Role.PERSONAL);
        return entityManager.persistAndFlush(user);
    }
}
