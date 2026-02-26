package com.revpay.transaction.dto;

import com.revpay.domain.enums.MoneyRequestStatus;

public class MoneyRequestResponseDto {
    private Double amount;
    private String purpose;
    private String moneyRequestStatus;
    private String senderWalUnqId;
    private String receiverWalUnqId;
}
