package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rev.app.dto.UserDTO;
import com.rev.app.dto.WalletDTO;
import com.rev.app.mapper.UserMapper;
import com.rev.app.repository.ITransactionRepository;
import com.rev.app.repository.IUserRepository;
import com.rev.app.service.ILoanApplicationService;
import com.rev.app.service.IUserService;
import com.rev.app.service.IWalletService;

class AdminRestControllerTest {

    private IUserRepository userRepository;
    private ITransactionRepository transactionRepository;
    private UserMapper userMapper;
    private IWalletService walletService;
    private ILoanApplicationService loanService;
    private IUserService userService;
    private AdminRestController adminRestController;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        transactionRepository = mock(ITransactionRepository.class);
        userMapper = mock(UserMapper.class);
        walletService = mock(IWalletService.class);
        loanService = mock(ILoanApplicationService.class);
        userService = mock(IUserService.class);
        adminRestController = new AdminRestController(userRepository, transactionRepository, userMapper, walletService, loanService, userService);
    }

    @Test
    void getSystemMetrics_Success() {
        when(userRepository.count()).thenReturn(10L);
        when(transactionRepository.getTotalTransactionVolume()).thenReturn(new BigDecimal("1000.00"));
        when(transactionRepository.count()).thenReturn(50L);

        ResponseEntity<Map<String, Object>> response = adminRestController.getSystemMetrics();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10L, response.getBody().get("totalUsers"));
        assertEquals(new BigDecimal("1000.00"), response.getBody().get("totalVolume"));
    }

    @Test
    void getAllUsersPaginated_Success() {
        org.springframework.data.domain.Page<UserDTO> emptyPage = new org.springframework.data.domain.PageImpl<>(java.util.Collections.emptyList());
        when(userService.getAllUsersPaginated(0, 10, "id", "asc", "")).thenReturn(emptyPage);

        ResponseEntity<org.springframework.data.domain.Page<UserDTO>> response = adminRestController.getAllUsersPaginated(0, 10, "id", "asc", "");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getContent().size());
    }

    @Test
    void addFunds_Success() {
        Map<String, BigDecimal> request = Collections.singletonMap("amount", new BigDecimal("100.00"));
        WalletDTO walletDTO = new WalletDTO();

        when(walletService.adminAddFunds(1L, new BigDecimal("100.00"))).thenReturn(walletDTO);

        ResponseEntity<WalletDTO> response = adminRestController.addFunds(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(walletService).adminAddFunds(1L, new BigDecimal("100.00"));
    }
}
