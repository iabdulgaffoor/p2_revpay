package com.revpay.domain.repository;

import com.revpay.domain.model.Loan;
import com.revpay.domain.model.enums.TransactionStatus;
import java.util.List;
import java.util.Optional;

public interface ILoanRepo {
    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    List<Loan> findByUserId(Long userId);

    List<Loan> findByStatus(TransactionStatus status);
}
