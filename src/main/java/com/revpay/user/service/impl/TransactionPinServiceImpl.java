package com.revpay.user.service.impl;

import com.revpay.domain.entity.user.TransactionPin;
import com.revpay.domain.entity.user.User;
import com.revpay.user.repository.ITransactionPinRepo;
import com.revpay.user.service.ITransactionPinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionPinServiceImpl implements ITransactionPinService {

    private final ITransactionPinRepo iTransactionPinRepo;

    @Override
    public void addTransactionPin(String transactionPin, User user) {
        iTransactionPinRepo.save(new TransactionPin(transactionPin, user));
    }

}
