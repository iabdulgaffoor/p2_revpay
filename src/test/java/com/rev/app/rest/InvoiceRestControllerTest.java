package com.rev.app.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rev.app.dto.InvoiceDTO;
import com.rev.app.service.IInvoiceService;

class InvoiceRestControllerTest {

    private IInvoiceService invoiceService;
    private InvoiceRestController invoiceRestController;

    @BeforeEach
    void setUp() {
        invoiceService = mock(IInvoiceService.class);
        invoiceRestController = new InvoiceRestController(invoiceService);
    }

    @Test
    void createInvoice_Success() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setCustomerName("Customer");

        when(invoiceService.createInvoice(anyLong(), any())).thenReturn(invoiceDTO);

        ResponseEntity<InvoiceDTO> response = invoiceRestController.createInvoice(1L, invoiceDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Customer", response.getBody().getCustomerName());
    }

    @Test
    void getInvoiceById_Success() {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(700L);

        when(invoiceService.getInvoiceById(700L)).thenReturn(invoiceDTO);

        ResponseEntity<InvoiceDTO> response = invoiceRestController.getInvoiceById(700L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(700L, response.getBody().getId());
    }
}
