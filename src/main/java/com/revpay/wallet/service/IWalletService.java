package com.revpay.wallet.service;

import com.revpay.domain.entity.user.User;

public interface IWalletService {

    void createWallet(User user, Double initialBalance);

}
