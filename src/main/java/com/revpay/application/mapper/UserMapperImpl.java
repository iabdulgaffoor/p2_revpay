package com.revpay.application.mapper;

import com.revpay.application.dto.AuthResponseDto;
import com.revpay.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements IUserMapper {

    @Override
    public AuthResponseDto toAuthResponseDto(User user, String token) {
        if (user == null)
            return null;
        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .build();
    }
}
