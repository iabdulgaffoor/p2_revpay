package com.revpay.application.service;

import com.revpay.application.dto.AuthResponseDto;
import com.revpay.application.dto.RegisterRequestDto;
import com.revpay.domain.model.Role;
import com.revpay.domain.model.User;
import com.revpay.domain.model.enums.AccountType;
import com.revpay.domain.repository.IRoleRepo;
import com.revpay.domain.repository.IUserRepo;
import com.revpay.infrastructure.security.IJwtUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceSmokeTest {

    @Mock
    private IUserRepo userRepo;
    @Mock
    private IRoleRepo roleRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private IJwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterPersonalUser() {
        // Arrange
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test@example.com")
                .password("password123")
                .fullName("Test User")
                .phone("1234567890")
                .accountType(AccountType.PERSONAL)
                .build();

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(roleRepo.findByName(anyString())).thenReturn(Optional.of(Role.builder().name("PERSONAL").build()));
        when(jwtUtils.generateToken(any())).thenReturn("mock_token");

        // Act
        AuthResponseDto response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
        assertEquals("mock_token", response.getToken());
        verify(userRepo, times(1)).save(any(User.class));
    }
}
