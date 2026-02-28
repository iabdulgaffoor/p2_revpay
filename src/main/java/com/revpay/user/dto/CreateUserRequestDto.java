package com.revpay.user.dto;

import lombok.Data;

@Data
public class CreateUserRequestDto {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String passKey;
    private Long securityQuestionId;
    private String userSecurityAnswer;
    private Double walletBalance;
    private Integer transactionPin;
}
