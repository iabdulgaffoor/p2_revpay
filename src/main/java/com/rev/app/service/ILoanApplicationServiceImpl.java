package com.rev.app.service;

import com.rev.app.dto.LoanApplicationDTO;
import com.rev.app.entity.LoanApplication;
import com.rev.app.entity.LoanApplication.LoanStatus;
import com.rev.app.entity.User;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.LoanApplicationMapper;
import com.rev.app.repository.ILoanApplicationRepository;
import com.rev.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ILoanApplicationServiceImpl implements ILoanApplicationService {

    private final ILoanApplicationRepository loanApplicationRepository;
    private final IUserRepository userRepository;
    private final LoanApplicationMapper loanApplicationMapper;
    private final IWalletService walletService; // To deposit funds upon approval

    @Autowired
    public ILoanApplicationServiceImpl(ILoanApplicationRepository loanApplicationRepository, 
                                       IUserRepository userRepository,
                                       LoanApplicationMapper loanApplicationMapper,
                                       IWalletService walletService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
        this.loanApplicationMapper = loanApplicationMapper;
        this.walletService = walletService;
    }

    @Override
    @Transactional
    public LoanApplicationDTO applyForLoan(Long businessUserId, LoanApplicationDTO loanApplicationDTO) {
        User businessUser = userRepository.findById(businessUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Business user not found"));

        if (businessUser.getRole() != User.Role.BUSINESS) {
            throw new BadRequestException("Only business accounts can apply for loans");
        }

        LoanApplication loan = loanApplicationMapper.toEntity(loanApplicationDTO);
        loan.setBusinessUser(businessUser);
        loan.setStatus(LoanStatus.PENDING);
        loan.setAppliedAt(java.time.LocalDateTime.now()); // Ensure NOT NULL constraint is met
        
        // Hardcode dummy interest rate for simulation
        loan.setInterestRate(new BigDecimal("9.5"));
        
        // Basic EMI calculation simulation (Amount / Months) * Interest Factor
        BigDecimal principal = loan.getAmount();
        BigDecimal months = new BigDecimal(loan.getTenureMonths());
        BigDecimal emi = principal.divide(months, 2, java.math.RoundingMode.HALF_UP)
                                  .multiply(new BigDecimal("1.05")); // Dummy formula
        loan.setEmi(emi);

        return loanApplicationMapper.toDTO(loanApplicationRepository.save(loan));
    }

    @Override
    public LoanApplicationDTO getLoanApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .map(loanApplicationMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found: " + id));
    }

    @Override
    public List<LoanApplicationDTO> getLoanApplicationsByBusinessUserId(Long businessUserId) {
        return loanApplicationRepository.findByBusinessUserId(businessUserId).stream()
                .map(loanApplicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanApplicationDTO> getAllLoanApplications() {
        return loanApplicationRepository.findAll().stream()
                .map(loanApplicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoanApplicationDTO updateLoanStatus(Long id, LoanStatus status) {
        LoanApplication loan = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found: " + id));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BadRequestException("Loan status is already " + loan.getStatus());
        }

        loan.setStatus(status);

        if (status == LoanStatus.APPROVED) {
            // Deposit funds to wallet
            walletService.addFunds(loan.getBusinessUser().getId(), loan.getAmount(), null);
        }

        return loanApplicationMapper.toDTO(loanApplicationRepository.save(loan));
    }

    @Override
    public java.util.Map<String, Object> simulateLoanRepayment(BigDecimal principal, Integer tenureMonths, BigDecimal annInterestRate) {
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Principal must be greater than zero");
        }
        if (tenureMonths == null || tenureMonths <= 0) {
            throw new BadRequestException("Tenure must be greater than zero");
        }
        if (annInterestRate == null || annInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Interest rate must be zero or positive");
        }

        // Standard EMI calculation: P * r * (1 + r)^n / ((1 + r)^n - 1)
        // r = monthly interest rate
        BigDecimal monthlyInterestRate = annInterestRate.divide(new BigDecimal("1200"), 10, java.math.RoundingMode.HALF_UP);
        
        BigDecimal emi;
        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            emi = principal.divide(new BigDecimal(tenureMonths), 2, java.math.RoundingMode.HALF_UP);
        } else {
            BigDecimal onePlusRToN = BigDecimal.ONE.add(monthlyInterestRate).pow(tenureMonths);
            BigDecimal numerator = principal.multiply(monthlyInterestRate).multiply(onePlusRToN);
            BigDecimal denominator = onePlusRToN.subtract(BigDecimal.ONE);
            emi = numerator.divide(denominator, 2, java.math.RoundingMode.HALF_UP);
        }

        BigDecimal totalPayment = emi.multiply(new BigDecimal(tenureMonths));
        BigDecimal totalInterest = totalPayment.subtract(principal);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("principal", principal);
        response.put("tenureMonths", tenureMonths);
        response.put("annualInterestRate", annInterestRate);
        response.put("monthlyEMI", emi);
        response.put("totalInterest", totalInterest);
        response.put("totalPayment", totalPayment);
        
        return response;
    }
}
