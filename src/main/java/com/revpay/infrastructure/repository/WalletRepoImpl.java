package com.revpay.infrastructure.repository;

import com.revpay.domain.model.Wallet;
import com.revpay.domain.repository.IWalletRepo;
import com.revpay.infrastructure.persistence.IJpaWalletRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalletRepoImpl implements IWalletRepo {
    private final IJpaWalletRepo jpaWalletRepo;

    @Override
    public Optional<Wallet> findByUserId(Long userId) {
        return jpaWalletRepo.findByUserId(userId);
    }

    @Override
    public Wallet save(Wallet wallet) {
        return jpaWalletRepo.save(wallet);
    }
}
