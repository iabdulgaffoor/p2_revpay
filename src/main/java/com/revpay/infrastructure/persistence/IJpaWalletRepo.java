package com.revpay.infrastructure.persistence;

import com.revpay.domain.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IJpaWalletRepo extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
}
