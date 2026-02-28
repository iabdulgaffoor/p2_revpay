package com.revpay.user.service;

import com.revpay.user.dto.CreateUserRequestDto;
import com.revpay.domain.entity.user.User;
import com.revpay.user.dto.UserProfileResponseDto;

public interface IUserService {

    User registerUser(CreateUserRequestDto userDto);

}
