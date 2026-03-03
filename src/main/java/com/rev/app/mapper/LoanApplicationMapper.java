package com.rev.app.mapper;

import com.rev.app.dto.LoanApplicationDTO;
import com.rev.app.entity.LoanApplication;
import org.springframework.stereotype.Component;

@Component
public class LoanApplicationMapper {

    public LoanApplicationDTO toDTO(LoanApplication loanApplication) {
        if (loanApplication == null) {
            return null;
        }

        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setId(loanApplication.getId());
        dto.setAmount(loanApplication.getAmount());
        dto.setPurpose(loanApplication.getPurpose());
        dto.setTenureMonths(loanApplication.getTenureMonths());
        dto.setStatus(loanApplication.getStatus());
        dto.setInterestRate(loanApplication.getInterestRate());
        dto.setEmi(loanApplication.getEmi());
        dto.setAppliedAt(loanApplication.getAppliedAt());

        if (loanApplication.getBusinessUser() != null) {
            dto.setBusinessUserId(loanApplication.getBusinessUser().getId());
            dto.setBusinessUserName(loanApplication.getBusinessUser().getBusinessName());
        }

        return dto;
    }

    public LoanApplication toEntity(LoanApplicationDTO dto) {
        if (dto == null) {
            return null;
        }

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(dto.getId());
        loanApplication.setAmount(dto.getAmount());
        loanApplication.setPurpose(dto.getPurpose());
        loanApplication.setTenureMonths(dto.getTenureMonths());
        loanApplication.setStatus(dto.getStatus());
        loanApplication.setInterestRate(dto.getInterestRate());
        loanApplication.setEmi(dto.getEmi());
        loanApplication.setAppliedAt(dto.getAppliedAt());

        return loanApplication;
    }
}
