package com.revpay.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.transaction.Transaction;

@Repository
public interface ITransactionRepo extends JpaRepository<Transaction, Long> {

}
