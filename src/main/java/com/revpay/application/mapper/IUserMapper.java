package com.revpay.application.mapper;

import com.revpay.application.dto.AuthResponseDto;
import com.revpay.domain.model.User;

public interface IUserMapper {
    AuthResponseDto toAuthResponseDto(User user, String token);
}
