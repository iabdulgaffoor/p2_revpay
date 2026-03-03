package com.rev.app.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.rev.app.entity.LoanApplication;
import com.rev.app.entity.LoanApplication.LoanStatus;
import com.rev.app.entity.User;
import com.rev.app.entity.User.Role;

@DataJpaTest
class ILoanApplicationRepositoryTest {

    @Autowired
    private ILoanApplicationRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByBusinessUserId_ReturnsLoans() {
        User businessUser = new User();
        businessUser.setEmail("biz@test.com");
        businessUser.setFullName("Biz User");
        businessUser.setPassword("pass");
        businessUser.setRole(Role.BUSINESS);
        businessUser = entityManager.persistAndFlush(businessUser);

        LoanApplication loan = new LoanApplication();
        loan.setBusinessUser(businessUser);
        loan.setAmount(new BigDecimal("5000.00"));
        loan.setTenureMonths(12);
        loan.setStatus(LoanStatus.PENDING);
        loan.setAppliedAt(java.time.LocalDateTime.now());
        entityManager.persistAndFlush(loan);

        List<LoanApplication> found = loanRepository.findByBusinessUserId(businessUser.getId());

        assertFalse(found.isEmpty());
        assertEquals(1, found.size());
    }
}
