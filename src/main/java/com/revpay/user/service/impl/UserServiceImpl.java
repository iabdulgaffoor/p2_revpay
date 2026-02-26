package com.revpay.user.service.impl;

import com.revpay.domain.entity.user.User;
import com.revpay.user.dto.CreateUserRequestDto;
import com.revpay.user.mapper.IUserMapper;
import com.revpay.user.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final IUserMapper iUserMapper;

    public UserServiceImpl(IUserMapper iUserMapper) {
        this.iUserMapper = iUserMapper;
    }

    @Override
    public User registerUser(CreateUserRequestDto userDto) {

        User user = iUserMapper.toEntity(userDto);



        return null;
    }
}
