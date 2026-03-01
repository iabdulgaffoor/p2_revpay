package com.revpay.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;

public interface IJwtUtils {
    String extractUsername(String token);

    String generateToken(UserDetails userDetails);

    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
}
