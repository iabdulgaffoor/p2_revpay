package com.revpay.common.exception.auth;

import java.io.Serial;

public class SecurityQuestionNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SecurityQuestionNotFoundException(String message) {
        super(message);
    }
}
