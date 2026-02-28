package com.revpay.user.service.impl;

import com.revpay.auth.repository.IUserSecurityAnswerRepo;
import com.revpay.auth.service.IPasswordService;
import com.revpay.auth.service.ISecurityAnswerService;
import com.revpay.common.exception.user.RoleNotFoundException;
import com.revpay.domain.entity.user.Role;
import com.revpay.domain.entity.user.User;
import com.revpay.user.dto.CreateUserRequestDto;
import com.revpay.user.dto.UserProfileResponseDto;
import com.revpay.user.mapper.IUserMapper;
import com.revpay.user.repository.IUserRepo;
import com.revpay.user.service.IRoleService;
import com.revpay.user.service.ITransactionPinService;
import com.revpay.user.service.IUserService;
import com.revpay.wallet.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserMapper iUserMapper;
    private final IUserRepo iUserRepo;
    private final IWalletService iWalletService;
    private final IRoleService iRoleService;
    private final IPasswordService iPasswordService;
    private final ISecurityAnswerService iSecurityAnswerService;
    private final ITransactionPinService iTransactionPinService;


    @Override
    @Transactional
    public User registerUser(CreateUserRequestDto userDto) {
        User user = iUserMapper.toEntity(userDto);

        Optional<Role> role = iRoleService.getUserById(1L);
        if (role.isEmpty()) {
            throw new RoleNotFoundException("Admin did not enter any Role");
        }
        Role retreivedRole = role.get();
        user.addRole(retreivedRole);
        user = iUserRepo.save(user);

        iPasswordService.addPassword(userDto.getPassKey(), user);
        iSecurityAnswerService.addUserSecurityAnswer(
                userDto.getUserSecurityAnswer(),
                user,
                userDto.getSecurityQuestionId()
        );
        iTransactionPinService.addTransactionPin(String.valueOf(userDto.getTransactionPin()), user);
        iWalletService.createWallet(user, userDto.getWalletBalance());

        return iUserRepo.save(user);
    }
}
