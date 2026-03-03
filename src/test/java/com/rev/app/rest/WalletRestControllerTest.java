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

import com.rev.app.dto.WalletDTO;
import com.rev.app.service.IWalletService;

class WalletRestControllerTest {

    private IWalletService walletService;
    private WalletRestController walletRestController;

    @BeforeEach
    void setUp() {
        walletService = mock(IWalletService.class);
        walletRestController = new WalletRestController(walletService);
    }

    @Test
    void getWalletByUserId_Success() {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId(10L);
        walletDTO.setBalance(new BigDecimal("100.00"));

        when(walletService.getWalletByUserId(1L)).thenReturn(walletDTO);

        ResponseEntity<WalletDTO> response = walletRestController.getWalletByUserId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("100.00"), response.getBody().getBalance());
    }

    @Test
    void addFunds_Success() {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setBalance(new BigDecimal("150.00"));

        when(walletService.addFunds(anyLong(), any(), any())).thenReturn(walletDTO);

        ResponseEntity<WalletDTO> response = walletRestController.addFunds(1L, new BigDecimal("50.00"), null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new BigDecimal("150.00"), response.getBody().getBalance());
    }
}
