package com.revpay.wallet.service.impl;

import com.revpay.common.util.WalletUtil;
import com.revpay.domain.entity.user.User;
import com.revpay.domain.entity.wallet.Wallet;
import com.revpay.wallet.repository.IWalletRepo;
import com.revpay.wallet.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements IWalletService {

    private final IWalletRepo iWalletRepo;
    private final WalletUtil walletUtil;

    @Override
    public void createWallet(User user, Double initialBalance) {
        String walletUnqId = walletUtil.unqIdGenerator();
        Wallet wallet = new Wallet(walletUnqId, initialBalance);
        user.addWallet(wallet);
        iWalletRepo.save(wallet);
    }
}
