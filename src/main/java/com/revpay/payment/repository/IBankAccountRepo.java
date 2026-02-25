package com.revpay.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.payment.BankAccount;

@Repository
public interface IBankAccountRepo extends JpaRepository<BankAccount, Long> {

}
