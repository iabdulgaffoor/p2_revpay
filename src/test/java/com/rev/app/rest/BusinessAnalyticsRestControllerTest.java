package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rev.app.service.IInvoiceService;
import com.rev.app.service.ITransactionService;
import com.rev.app.service.IUserService;

class BusinessAnalyticsRestControllerTest {

    private ITransactionService transactionService;
    private IInvoiceService invoiceService;
    private IUserService userService;
    private BusinessAnalyticsRestController businessAnalyticsRestController;

    @BeforeEach
    void setUp() {
        transactionService = mock(ITransactionService.class);
        invoiceService = mock(IInvoiceService.class);
        userService = mock(IUserService.class);
        businessAnalyticsRestController = new BusinessAnalyticsRestController(transactionService, invoiceService, userService);
    }

    @Test
    void getBusinessSummary_Success() {
        when(transactionService.getTransactionsByUserId(1L)).thenReturn(Collections.emptyList());
        when(invoiceService.getInvoicesByBusinessUserId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = businessAnalyticsRestController.getBusinessSummary(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(java.math.BigDecimal.ZERO, response.getBody().get("totalReceived"));
        assertEquals(java.math.BigDecimal.ZERO, response.getBody().get("totalSent"));
    }
}
