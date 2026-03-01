package com.revpay.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanRequestDto {
    private BigDecimal amount;
    private int tenureMonths;
    private String purpose;
}
