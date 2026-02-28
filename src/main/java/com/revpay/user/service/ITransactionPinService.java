package com.revpay.user.service;

import com.revpay.domain.entity.user.User;

public interface ITransactionPinService {
    void addTransactionPin(String transactionPin, User user);
}
