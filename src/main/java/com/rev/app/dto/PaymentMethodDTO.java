package com.rev.app.dto;

import com.rev.app.entity.PaymentMethod.PaymentMethodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private Long id;
    private Long userId;
    
    @NotNull(message = "Payment method type is required")
    private PaymentMethodType type;
    
    @NotBlank(message = "Account number is required")
    private String accountNumberMasked;
    
    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/?([0-9]{2})$", message = "Invalid expiry date format. Use MM/YY")
    private String expiryDate;
    
    @NotBlank(message = "Billing address is required")
    private String billingAddress;
    
    private boolean isDefault;
}
