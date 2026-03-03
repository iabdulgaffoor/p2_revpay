package com.rev.app.mapper;

import com.rev.app.dto.PaymentMethodDTO;
import com.rev.app.entity.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodMapper {

    public PaymentMethodDTO toDTO(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }

        PaymentMethodDTO dto = new PaymentMethodDTO();
        dto.setId(paymentMethod.getId());
        dto.setType(paymentMethod.getType());
        dto.setExpiryDate(paymentMethod.getExpiryDate());
        dto.setBillingAddress(paymentMethod.getBillingAddress());
        dto.setDefault(paymentMethod.isDefault());

        if (paymentMethod.getUser() != null) {
            dto.setUserId(paymentMethod.getUser().getId());
        }

        // Mask the account number for the DTO
        if (paymentMethod.getAccountNumber() != null && paymentMethod.getAccountNumber().length() >= 4) {
            String accNo = paymentMethod.getAccountNumber();
            dto.setAccountNumberMasked("****" + accNo.substring(accNo.length() - 4));
        }

        return dto;
    }

    public PaymentMethod toEntity(PaymentMethodDTO dto) {
        if (dto == null) {
            return null;
        }

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setId(dto.getId());
        paymentMethod.setType(dto.getType());
        paymentMethod.setExpiryDate(dto.getExpiryDate());
        paymentMethod.setBillingAddress(dto.getBillingAddress());
        paymentMethod.setDefault(dto.isDefault());

        // Note: User object and exact unmasked accountNumber/CVV must be managed by the Service layer.

        return paymentMethod;
    }
}
