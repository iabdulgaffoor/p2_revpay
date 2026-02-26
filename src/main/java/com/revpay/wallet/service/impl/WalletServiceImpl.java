package com.revpay.wallet.service.impl;

import com.revpay.domain.entity.user.User;
import com.revpay.domain.entity.wallet.Wallet;
import com.revpay.wallet.repository.IWalletRepo;
import com.revpay.wallet.service.IWalletService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletServiceImpl implements IWalletService {

    private final IWalletRepo iWalletRepo;

    public WalletServiceImpl(IWalletRepo iWalletRepo) {
        this.iWalletRepo = iWalletRepo;
    }

    @Override
    public User createWallet(User user) {

        Optional<Wallet> wallet = iWalletRepo.save()

        user.addWallet(wallet);
        return null;
    }

}
