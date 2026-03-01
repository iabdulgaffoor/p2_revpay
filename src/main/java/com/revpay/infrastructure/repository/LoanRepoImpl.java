package com.revpay.infrastructure.repository;

import com.revpay.domain.model.Loan;
import com.revpay.domain.model.enums.TransactionStatus;
import com.revpay.domain.repository.ILoanRepo;
import com.revpay.infrastructure.persistence.IJpaLoanRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LoanRepoImpl implements ILoanRepo {
    private final IJpaLoanRepo jpaRepo;

    @Override
    public Loan save(Loan loan) {
        return jpaRepo.save(loan);
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return jpaRepo.findById(id);
    }

    @Override
    public List<Loan> findByUserId(Long userId) {
        return jpaRepo.findByUserId(userId);
    }

    @Override
    public List<Loan> findByStatus(TransactionStatus status) {
        return jpaRepo.findByStatus(status);
    }
}
