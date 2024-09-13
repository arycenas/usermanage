package com.training.usermanage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.training.usermanage.model.Role;
import com.training.usermanage.model.User;
import com.training.usermanage.model.UserRedis;
import com.training.usermanage.request.LoginRequest;
import com.training.usermanage.request.RefreshTokenRequest;
import com.training.usermanage.request.RegisterRequest;
import com.training.usermanage.response.JwtResponse;
import com.training.usermanage.service.implement.AuthServiceImplement;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplementTest {

    @InjectMocks
    private AuthServiceImplement authServiceImplement;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private UserRedis userRedis;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        userRedis = new UserRedis();
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(Role.USER);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        refreshTokenRequest = new RefreshTokenRequest();
    }

    @Test
    void shouldRegisterUser() {
        given(passwordEncoder.encode(registerRequest.getPassword())).willReturn("encodedPassword");

        UserRedis registeredUser = authServiceImplement.register(registerRequest);

        userRedis.setPassword("encodedPassword");

        assertThat(registeredUser.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(registeredUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(registeredUser.getRole()).isEqualTo(Role.USER);

        verify(redisService).saveUser(registerRequest.getUsername(), userRedis);
    }

    @Test
    void shouldLoginSuccessfully() {
        given(redisService.getUser(loginRequest.getUsername())).willReturn(userRedis);
        given(passwordEncoder.matches(loginRequest.getPassword(), userRedis.getPassword())).willReturn(true);
        given(jwtService.generateToken(user)).willReturn("testToken");
        given(jwtService.generateRefreshToken(any(), any())).willReturn("testRefreshToken");

        JwtResponse jwtResponse = authServiceImplement.login(loginRequest);

        assertThat(jwtResponse.getToken()).isEqualTo("testToken");
        assertThat(jwtResponse.getRefreshToken()).isEqualTo("testRefreshToken");

        verify(redisService).saveToken("testToken", loginRequest.getUsername());
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        given(jwtService.extractUsername(refreshTokenRequest.getToken())).willReturn(userRedis.getUsername());
        given(redisService.getUser(userRedis.getUsername())).willReturn(userRedis);
        given(jwtService.isTokenValid(refreshTokenRequest.getToken(), user)).willReturn(true);
        given(jwtService.generateToken(user)).willReturn("newTestToken");

        JwtResponse jwtResponse = authServiceImplement.refreshToken(refreshTokenRequest);

        assertThat(jwtResponse.getToken()).isEqualTo("newTestToken");
        assertThat(jwtResponse.getRefreshToken()).isEqualTo(refreshTokenRequest.getToken());
    }
}
