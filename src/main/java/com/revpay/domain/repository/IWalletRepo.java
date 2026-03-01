package com.revpay.domain.repository;

import com.revpay.domain.model.Wallet;
import java.util.Optional;

public interface IWalletRepo {
    Optional<Wallet> findByUserId(Long userId);

    Wallet save(Wallet wallet);
}
