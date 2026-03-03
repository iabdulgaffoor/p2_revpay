package com.rev.app.dto;

import com.rev.app.entity.LoanApplication.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationDTO {
    private Long id;
    private Long businessUserId;
    private String businessUserName;
    private BigDecimal amount;
    private String purpose;
    private Integer tenureMonths;
    private LoanStatus status;
    private BigDecimal interestRate;
    private BigDecimal emi;
    private LocalDateTime appliedAt;
}
