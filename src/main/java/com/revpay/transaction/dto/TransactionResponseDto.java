package com.revpay.transaction.dto;

import com.revpay.domain.enums.TransactionStatus;

public class TransactionResponseDto {
    private String receiverWalUnqId;
    private String transactionType;
    private String transactionStatus;
    private Double sentAmount;
    private String totalBalance;
}