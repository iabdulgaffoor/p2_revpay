package com.revpay.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revpay.domain.entity.wallet.Wallet;

@Repository
public interface IWalletRepo extends JpaRepository<Wallet, Long> {

}
