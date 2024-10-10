package com.training.usermanage.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.training.usermanage.model.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtService jwtServiceImplement;

    private Key signingKey;
    private String testToken;

    @SuppressWarnings("deprecation")
    @BeforeEach
    void setUp() {
        byte[] key = Decoders.BASE64
                .decode("413F4428472B4B6250655368566D5970337336763979244226452948404D6351");
        signingKey = Keys.hmacShaKeyFor(key);

        testToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(signingKey)
                .compact();
    }

    @Test
    void shouldGenerateToken() {
        given(userDetails.getUsername()).willReturn("testuser");

        String token = jwtServiceImplement.generateToken(userDetails);

        assertThat(token).isNotNull();
    }

    @Test
    void shouldGenerateRefreshToken() {
        given(userDetails.getUsername()).willReturn("testuser");
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", Role.USER);

        String refreshToken = jwtServiceImplement.generateRefreshToken(extraClaims, userDetails);

        assertThat(refreshToken).isNotNull();
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String username = jwtServiceImplement.extractUsername(testToken);

        assertThat(username).isEqualTo("testuser");
    }

    @Test
    void shouldValidateTokenSuccessfully() {
        given(userDetails.getUsername()).willReturn("testuser");

        boolean isValid = jwtServiceImplement.isTokenValid(testToken, userDetails);

        assertThat(isValid).isTrue();
    }
}
