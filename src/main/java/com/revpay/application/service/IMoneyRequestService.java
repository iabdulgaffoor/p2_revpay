package com.revpay.application.service;

import com.revpay.application.dto.MoneyRequestDto;

public interface IMoneyRequestService {
    void sendRequest(Long requesterId, MoneyRequestDto request);

    void acceptRequest(Long requesteeId, Long requestId, String pin);

    void rejectRequest(Long requesteeId, Long requestId);
}
