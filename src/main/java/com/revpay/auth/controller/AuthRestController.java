package com.revpay.auth.controller;

import com.revpay.user.dto.CreateUserRequestDto;
import com.revpay.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRestController {

    private final IUserService iUserService;

    @PostMapping("/personal/save")
    public ResponseEntity<String> registerUser(
            @RequestBody CreateUserRequestDto createUserRequestDto
    ) {
        iUserService.registerUser(createUserRequestDto);
        return new ResponseEntity<>(
                "success",
                HttpStatus.CREATED
        );
    }
}
