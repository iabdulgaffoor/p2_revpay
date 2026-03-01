package com.revpay.application.service;

import com.revpay.application.dto.BusinessProfileRequestDto;
import com.revpay.application.dto.LoanRequestDto;

public interface IBusinessService {
    void applyForBusinessProfile(Long userId, BusinessProfileRequestDto request);

    void applyForLoan(Long userId, LoanRequestDto request);
}
