package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.MoneyRequest;
import com.rev.app.entity.MoneyRequest.RequestStatus;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class IMoneyRequestRepositoryTest {

    @Autowired
    private IMoneyRequestRepository moneyRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByRequesteeId_ReturnsRequests() {
        User requester = createTestUser("req1@t.com");
        User requestee = createTestUser("req2@t.com");

        MoneyRequest request = new MoneyRequest();
        request.setRequester(requester);
        request.setRequestee(requestee);
        request.setAmount(new BigDecimal("20.00"));
        request.setStatus(RequestStatus.PENDING);
        entityManager.persistAndFlush(request);

        List<MoneyRequest> found = moneyRequestRepository.findByRequesteeId(requestee.getId());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
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
