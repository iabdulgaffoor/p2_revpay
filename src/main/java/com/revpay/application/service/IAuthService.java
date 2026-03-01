package com.revpay.application.service;

import com.revpay.application.dto.AuthResponseDto;
import com.revpay.application.dto.LoginRequestDto;
import com.revpay.application.dto.RegisterRequestDto;

public interface IAuthService {
    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);
}
