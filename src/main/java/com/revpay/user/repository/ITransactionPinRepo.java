package com.revpay.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.user.TransactionPin;

@Repository
public interface ITransactionPinRepo extends JpaRepository<TransactionPin, Long> {

}
