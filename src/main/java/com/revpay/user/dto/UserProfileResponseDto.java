package com.revpay.user.dto;

import lombok.Data;

@Data
public class UserProfileResponseDto {
    private String fullName;
    private String email;
    private String phone;
    private String accountType;
    private String accountStatus;
}
