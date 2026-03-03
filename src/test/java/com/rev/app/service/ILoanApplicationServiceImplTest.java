package com.rev.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rev.app.dto.LoanApplicationDTO;
import com.rev.app.entity.LoanApplication;
import com.rev.app.entity.LoanApplication.LoanStatus;
import com.rev.app.entity.User;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.LoanApplicationMapper;
import com.rev.app.repository.ILoanApplicationRepository;
import com.rev.app.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class ILoanApplicationServiceImplTest {

    @Mock
    private ILoanApplicationRepository loanApplicationRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private LoanApplicationMapper loanApplicationMapper;

    @Mock
    private IWalletService walletService;

    @InjectMocks
    private ILoanApplicationServiceImpl loanService;

    private User businessUser;
    private LoanApplication loan;
    private LoanApplicationDTO loanDTO;

    @BeforeEach
    void setUp() {
        businessUser = new User();
        businessUser.setId(3L);
        businessUser.setRole(User.Role.BUSINESS);

        loan = new LoanApplication();
        loan.setId(5L);
        loan.setBusinessUser(businessUser);
        loan.setAmount(new BigDecimal("10000.00"));
        loan.setTenureMonths(12);
        loan.setStatus(LoanStatus.PENDING);

        loanDTO = new LoanApplicationDTO();
        loanDTO.setId(5L);
        loanDTO.setAmount(new BigDecimal("10000.00"));
        loanDTO.setTenureMonths(12);
    }

    @Test
    void applyForLoan_Success() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(businessUser));
        when(loanApplicationMapper.toEntity(loanDTO)).thenReturn(loan);
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loan);
        when(loanApplicationMapper.toDTO(any(LoanApplication.class))).thenReturn(loanDTO);

        LoanApplicationDTO result = loanService.applyForLoan(3L, loanDTO);

        assertNotNull(result);
        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void applyForLoan_NotBusiness_ThrowsException() {
        businessUser.setRole(User.Role.PERSONAL);
        when(userRepository.findById(3L)).thenReturn(Optional.of(businessUser));

        assertThrows(BadRequestException.class, () -> loanService.applyForLoan(3L, loanDTO));
    }

    @Test
    void updateLoanStatus_Approved_Success() {
        when(loanApplicationRepository.findById(5L)).thenReturn(Optional.of(loan));
        when(loanApplicationRepository.save(any(LoanApplication.class))).thenReturn(loan);
        when(loanApplicationMapper.toDTO(loan)).thenReturn(loanDTO);

        LoanApplicationDTO result = loanService.updateLoanStatus(5L, LoanStatus.APPROVED);

        assertNotNull(result);
        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        verify(walletService).addFunds(eq(3L), any(BigDecimal.class), eq(null));
    }

    @Test
    void simulateLoanRepayment_Success() {
        BigDecimal principal = new BigDecimal("100000.00");
        Integer tenure = 12;
        BigDecimal interestRate = new BigDecimal("12.00");

        Map<String, Object> result = loanService.simulateLoanRepayment(principal, tenure, interestRate);

        assertNotNull(result);
        assertEquals(new BigDecimal("8884.88"), result.get("monthlyEMI"));
    }
}
