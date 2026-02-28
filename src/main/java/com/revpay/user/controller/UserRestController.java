package com.revpay.user.controller;

import com.revpay.user.dto.CreateUserRequestDto;
import com.revpay.user.dto.UserProfileResponseDto;
import com.revpay.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserService iUserService;

    @PostMapping("/register-personal")
    public ResponseEntity<UserProfileResponseDto> registerUser(
            @RequestBody CreateUserRequestDto createUserRequestDto
    ) {
        UserProfileResponseDto userProfileResponseDto =
                iUserService.registerUser(createUserRequestDto);
        return new ResponseEntity<>(
                userProfileResponseDto,
                HttpStatus.CREATED
        );
    }
}
