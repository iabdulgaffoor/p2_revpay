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

import com.rev.app.dto.PaymentMethodDTO;
import com.rev.app.entity.PaymentMethod;
import com.rev.app.entity.User;
import com.rev.app.exception.ResourceNotFoundException;
import com.rev.app.mapper.PaymentMethodMapper;
import com.rev.app.repository.IPaymentMethodRepository;
import com.rev.app.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class IPaymentMethodServiceImplTest {

    @Mock
    private IPaymentMethodRepository paymentMethodRepository;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private IPaymentMethodServiceImpl paymentMethodService;

    private User user;
    private PaymentMethod paymentMethod;
    private PaymentMethodDTO paymentMethodDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        paymentMethod = new PaymentMethod();
        paymentMethod.setId(10L);
        paymentMethod.setUser(user);
        paymentMethod.setDefault(false);

        paymentMethodDTO = new PaymentMethodDTO();
        paymentMethodDTO.setId(10L);
        paymentMethodDTO.setDefault(false);
    }

    @Test
    void addPaymentMethod_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentMethodRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(paymentMethodMapper.toEntity(paymentMethodDTO)).thenReturn(paymentMethod);
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(paymentMethod);
        when(paymentMethodMapper.toDTO(any(PaymentMethod.class))).thenReturn(paymentMethodDTO);

        PaymentMethodDTO result = paymentMethodService.addPaymentMethod(1L, paymentMethodDTO);

        assertNotNull(result);
        assertTrue(paymentMethod.isDefault()); // Should be set as default because it's first
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void getPaymentMethodsByUserId_Success() {
        when(paymentMethodRepository.findByUserId(1L)).thenReturn(Collections.singletonList(paymentMethod));
        when(paymentMethodMapper.toDTO(paymentMethod)).thenReturn(paymentMethodDTO);

        List<PaymentMethodDTO> result = paymentMethodService.getPaymentMethodsByUserId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void setDefaultPaymentMethod_Success() {
        when(paymentMethodRepository.findById(10L)).thenReturn(Optional.of(paymentMethod));
        when(paymentMethodRepository.findByUserId(1L)).thenReturn(Collections.singletonList(paymentMethod));
        when(paymentMethodRepository.saveAll(anyList())).thenReturn(Collections.singletonList(paymentMethod));
        when(paymentMethodMapper.toDTO(paymentMethod)).thenReturn(paymentMethodDTO);

        PaymentMethodDTO result = paymentMethodService.setDefaultPaymentMethod(10L, 1L);

        assertNotNull(result);
        assertTrue(paymentMethod.isDefault());
        verify(paymentMethodRepository).saveAll(anyList());
    }

    @Test
    void deletePaymentMethod_Success() {
        when(paymentMethodRepository.findById(10L)).thenReturn(Optional.of(paymentMethod));
        doNothing().when(paymentMethodRepository).deleteById(10L);

        assertDoesNotThrow(() -> paymentMethodService.deletePaymentMethod(10L, 1L));
        verify(paymentMethodRepository).deleteById(10L);
    }
}
