package com.revpay.infrastructure.persistence;

import com.revpay.domain.model.Loan;
import com.revpay.domain.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IJpaLoanRepo extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);

    List<Loan> findByStatus(TransactionStatus status);
}
