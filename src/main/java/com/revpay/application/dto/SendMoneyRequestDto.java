package com.revpay.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMoneyRequestDto {
    private Long receiverId;

    private String receiverIdentifier;

    @NotNull
    @DecimalMin(value = "1.00", message = "Minimum amount is 1.00")
    private BigDecimal amount;

    private String description;

    @NotNull
    private String pin;
}
