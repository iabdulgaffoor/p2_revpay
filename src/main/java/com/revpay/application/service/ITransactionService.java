package com.revpay.application.service;

import com.revpay.application.dto.SendMoneyRequestDto;

public interface ITransactionService {
    void sendMoney(Long senderId, SendMoneyRequestDto request);
}
