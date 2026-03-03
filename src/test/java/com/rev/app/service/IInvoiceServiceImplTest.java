package com.rev.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rev.app.dto.InvoiceDTO;
import com.rev.app.entity.Invoice;
import com.rev.app.entity.Invoice.InvoiceStatus;
import com.rev.app.entity.User;
import com.rev.app.exception.BadRequestException;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.InvoiceMapper;
import com.rev.app.repository.IInvoiceRepository;
import com.rev.app.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class IInvoiceServiceImplTest {

    @Mock
    private IInvoiceRepository invoiceRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private INotificationService notificationService;

    @InjectMocks
    private IInvoiceServiceImpl invoiceService;

    private User businessUser;
    private Invoice invoice;
    private InvoiceDTO invoiceDTO;

    @BeforeEach
    void setUp() {
        businessUser = new User();
        businessUser.setId(3L);
        businessUser.setRole(User.Role.BUSINESS);

        invoice = new Invoice();
        invoice.setId(20L);
        invoice.setBusinessUser(businessUser);
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setCustomerEmail("customer@example.com");

        invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(20L);
    }

    @Test
    void createInvoice_Success() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(businessUser));
        when(invoiceMapper.toEntity(invoiceDTO)).thenReturn(invoice);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toDTO(any(Invoice.class))).thenReturn(invoiceDTO);

        InvoiceDTO result = invoiceService.createInvoice(3L, invoiceDTO);

        assertNotNull(result);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void createInvoice_NotBusiness_ThrowsException() {
        businessUser.setRole(User.Role.PERSONAL);
        when(userRepository.findById(3L)).thenReturn(Optional.of(businessUser));

        assertThrows(BadRequestException.class, () -> invoiceService.createInvoice(3L, invoiceDTO));
    }

    @Test
    void updateInvoiceStatus_Success() {
        when(invoiceRepository.findById(20L)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toDTO(invoice)).thenReturn(invoiceDTO);

        InvoiceDTO result = invoiceService.updateInvoiceStatus(20L, InvoiceStatus.PAID);

        assertNotNull(result);
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }

    @Test
    void sendInvoiceNotification_Success() {
        businessUser.setBusinessName("Test Biz");
        when(invoiceRepository.findById(20L)).thenReturn(Optional.of(invoice));
        User customer = new User();
        customer.setId(5L);
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        
        // Also mocks updateInvoiceStatus inside
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);
        when(invoiceMapper.toDTO(invoice)).thenReturn(invoiceDTO);

        assertDoesNotThrow(() -> invoiceService.sendInvoiceNotification(20L));
        verify(notificationService).createNotification(anyLong(), anyString(), any());
        assertEquals(InvoiceStatus.SENT, invoice.getStatus());
    }
}
