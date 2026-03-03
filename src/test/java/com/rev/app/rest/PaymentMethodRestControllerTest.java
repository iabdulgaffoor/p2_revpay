package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rev.app.dto.PaymentMethodDTO;
import com.rev.app.service.IPaymentMethodService;

class PaymentMethodRestControllerTest {

    private IPaymentMethodService paymentMethodService;
    private PaymentMethodRestController paymentMethodRestController;

    @BeforeEach
    void setUp() {
        paymentMethodService = mock(IPaymentMethodService.class);
        paymentMethodRestController = new PaymentMethodRestController(paymentMethodService);
    }

    @Test
    void addPaymentMethod_Success() {
        PaymentMethodDTO pmDTO = new PaymentMethodDTO();
        pmDTO.setType(com.rev.app.entity.PaymentMethod.PaymentMethodType.CREDIT_CARD);

        when(paymentMethodService.addPaymentMethod(anyLong(), any())).thenReturn(pmDTO);

        ResponseEntity<PaymentMethodDTO> response = paymentMethodRestController.addPaymentMethod(1L, pmDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(com.rev.app.entity.PaymentMethod.PaymentMethodType.CREDIT_CARD, response.getBody().getType());
    }

    @Test
    void getPaymentMethodsByUser_Success() {
        when(paymentMethodService.getPaymentMethodsByUserId(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentMethodDTO>> response = paymentMethodRestController.getPaymentMethodsByUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
    }
}
