package com.revpay.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.auth.TransactionPin;

@Repository
public interface ITransactionPinRepo extends JpaRepository<TransactionPin, Long> {

}
