package com.rev.app.service;

import com.rev.app.dto.LoanApplicationDTO;

import java.math.BigDecimal;
import java.util.List;

import java.util.Map;

public interface ILoanApplicationService {
    LoanApplicationDTO applyForLoan(Long businessUserId, LoanApplicationDTO loanApplicationDTO);
    LoanApplicationDTO getLoanApplicationById(Long id);
    List<LoanApplicationDTO> getLoanApplicationsByBusinessUserId(Long businessUserId);
    List<LoanApplicationDTO> getAllLoanApplications();
    LoanApplicationDTO updateLoanStatus(Long id, com.rev.app.entity.LoanApplication.LoanStatus status);
    Map<String, Object> simulateLoanRepayment(BigDecimal principal, Integer tenureMonths, BigDecimal interestRate);
}
