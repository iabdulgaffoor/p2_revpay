package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rev.app.dto.MoneyRequestDTO;
import com.rev.app.service.IMoneyRequestService;

class MoneyRequestRestControllerTest {

    private IMoneyRequestService moneyRequestService;
    private MoneyRequestRestController moneyRequestRestController;

    @BeforeEach
    void setUp() {
        moneyRequestService = mock(IMoneyRequestService.class);
        moneyRequestRestController = new MoneyRequestRestController(moneyRequestService);
    }

    @Test
    void sendRequest_Success() {
        MoneyRequestRestController.SendMoneyReq request = new MoneyRequestRestController.SendMoneyReq();
        request.setRequesterId(1L);
        request.setRequesteeId(2L);
        request.setAmount(new BigDecimal("25.00"));
        request.setPurpose("Coffee");

        MoneyRequestDTO dto = new MoneyRequestDTO();
        dto.setId(300L);

        when(moneyRequestService.sendRequest(any(), any(), any(), any())).thenReturn(dto);

        ResponseEntity<MoneyRequestDTO> response = moneyRequestRestController.sendRequest(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(300L, response.getBody().getId());
    }

    @Test
    void getIncomingRequests_Success() {
        when(moneyRequestService.getIncomingRequests(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<MoneyRequestDTO>> response = moneyRequestRestController.getIncomingRequests(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }
}
