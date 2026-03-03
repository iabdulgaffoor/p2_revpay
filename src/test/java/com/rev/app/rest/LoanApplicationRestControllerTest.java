package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rev.app.dto.LoanApplicationDTO;
import com.rev.app.service.ILoanApplicationService;

class LoanApplicationRestControllerTest {

    private ILoanApplicationService loanApplicationService;
    private LoanApplicationRestController loanApplicationRestController;

    @BeforeEach
    void setUp() {
        loanApplicationService = mock(ILoanApplicationService.class);
        loanApplicationRestController = new LoanApplicationRestController(loanApplicationService);
    }

    @Test
    void applyForLoan_Success() {
        LoanApplicationDTO loanDTO = new LoanApplicationDTO();
        loanDTO.setAmount(new BigDecimal("10000.00"));

        when(loanApplicationService.applyForLoan(anyLong(), any())).thenReturn(loanDTO);

        ResponseEntity<LoanApplicationDTO> response = loanApplicationRestController.applyForLoan(1L, loanDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(new BigDecimal("10000.00"), response.getBody().getAmount());
    }

    @Test
    void getLoanApplicationById_Success() {
        LoanApplicationDTO loanDTO = new LoanApplicationDTO();
        loanDTO.setId(500L);

        when(loanApplicationService.getLoanApplicationById(500L)).thenReturn(loanDTO);

        ResponseEntity<LoanApplicationDTO> response = loanApplicationRestController.getLoanApplicationById(500L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(500L, response.getBody().getId());
    }
}
