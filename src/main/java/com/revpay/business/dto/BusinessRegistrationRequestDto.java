package com.revpay.business.dto;

import com.revpay.domain.enums.BusinessType;

public class BusinessRegistrationRequestDto {
    private String personalAccountEmail;
    private String walUnqId;
    private String businessName;
    private BusinessType businessType;
    private String taxId;
    private String address;
}
