package com.rev.app.service;

import com.rev.app.dto.PaymentMethodDTO;
import java.util.List;

public interface IPaymentMethodService {
    PaymentMethodDTO addPaymentMethod(Long userId, PaymentMethodDTO paymentMethodDTO);
    PaymentMethodDTO getPaymentMethodById(Long id);
    List<PaymentMethodDTO> getPaymentMethodsByUserId(Long userId);
    PaymentMethodDTO updatePaymentMethod(Long id, PaymentMethodDTO paymentMethodDTO);
    void deletePaymentMethod(Long id, Long userId);
    PaymentMethodDTO setDefaultPaymentMethod(Long id, Long userId);
}
