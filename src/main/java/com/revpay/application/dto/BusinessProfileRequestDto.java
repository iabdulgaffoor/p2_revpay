package com.revpay.application.dto;

import lombok.Data;

@Data
public class BusinessProfileRequestDto {
    private String businessName;
    private String businessType;
    private String taxId;
    private String businessAddress;
}
