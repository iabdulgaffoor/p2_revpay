package com.revpay.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.loan.Loan;

@Repository
public interface ILoanRepo extends JpaRepository<Loan, Long> {

}
