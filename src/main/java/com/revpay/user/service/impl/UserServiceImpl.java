package com.revpay.user.service.impl;

import com.revpay.domain.entity.user.Role;
import com.revpay.domain.entity.user.User;
import com.revpay.user.dto.CreateUserRequestDto;
import com.revpay.user.dto.UserProfileResponseDto;
import com.revpay.user.mapper.IUserMapper;
import com.revpay.user.repository.IUserRepo;
import com.revpay.user.service.IRoleSevice;
import com.revpay.user.service.IUserService;
import com.revpay.wallet.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserMapper iUserMapper;
    private final IUserRepo iUserRepo;
    private final IWalletService iWalletService;
    private final IRoleSevice iRoleSevice;

    @Override
    public UserProfileResponseDto registerUser(CreateUserRequestDto userDto) {
        User user = iUserMapper.toEntity(userDto);
        //Role role = iRoleSevice.getRole();
        //role = iRoleSevice.saveRole(role);
        //user.addRole(role);
        user = iUserRepo.save(user);
        user = iWalletService.createWallet(user, 2000d);
        User savedUser = iUserRepo.save(user);
        return iUserMapper.toResponseDto(savedUser);
    }
}
