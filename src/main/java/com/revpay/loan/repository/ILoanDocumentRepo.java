package com.revpay.loan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.loan.LoanDocument;

@Repository
public interface ILoanDocumentRepo extends JpaRepository<LoanDocument, Long> {

}
