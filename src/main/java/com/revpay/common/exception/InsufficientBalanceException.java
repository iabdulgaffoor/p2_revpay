package com.revpay.common.exception;

import org.springframework.http.HttpStatus;

public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException() {
        super("Insufficient balance for this transaction", "INSUFFICIENT_BALANCE", HttpStatus.BAD_REQUEST);
    }
}
