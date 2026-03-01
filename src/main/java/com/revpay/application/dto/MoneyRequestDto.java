package com.revpay.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MoneyRequestDto {
    private String requestedFromEmail;
    private BigDecimal amount;
    private String purpose;
}
