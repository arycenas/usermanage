package com.training.usermanage.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.training.usermanage.model.Role;
import com.training.usermanage.model.User;
import com.training.usermanage.model.UserRedis;
import com.training.usermanage.request.TokenRequest;
import com.training.usermanage.request.UserRequest;
import com.training.usermanage.response.JwtResponse;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisService redisService;

    private UserRequest registerRequest;
    private UserRequest loginRequest;
    private TokenRequest refreshTokenRequest;
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

        registerRequest = new UserRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");

        loginRequest = new UserRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        refreshTokenRequest = new TokenRequest();
        refreshTokenRequest.setToken("testRefreshToken"); // Token assignment for refresh tests
    }

    @Test
    void shouldRegisterUser() {
        // Mock the password encoding
        given(passwordEncoder.encode(registerRequest.getPassword())).willReturn("encodedPassword");

        // Call the register method
        UserRedis registeredUser = authService.register(registerRequest);

        // Capture the UserRedis object passed to the saveUser method
        ArgumentCaptor<UserRedis> userRedisCaptor = ArgumentCaptor.forClass(UserRedis.class);
        verify(redisService).saveUser(eq(registerRequest.getUsername()), userRedisCaptor.capture());

        // Get the captured UserRedis object and assert its values
        UserRedis capturedUserRedis = userRedisCaptor.getValue();
        assertThat(capturedUserRedis.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(capturedUserRedis.getPassword()).isEqualTo("encodedPassword");
        assertThat(capturedUserRedis.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void shouldLoginSuccessfully() {
        given(redisService.getUser(loginRequest.getUsername())).willReturn(userRedis);
        given(passwordEncoder.matches(loginRequest.getPassword(), userRedis.getPassword())).willReturn(true);

        // Ensure that the user object used in stubbing is exactly the same one used in
        // the test
        given(jwtService.generateToken(any(User.class))).willReturn("testToken");
        given(jwtService.generateRefreshToken(any(), any())).willReturn("testRefreshToken");

        JwtResponse jwtResponse = authService.login(loginRequest);

        assertThat(jwtResponse.getToken()).isEqualTo("testToken");
        assertThat(jwtResponse.getRefreshToken()).isEqualTo("testRefreshToken");

        verify(redisService).saveToken("testToken", loginRequest.getUsername());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldFailLoginWithInvalidPassword() {
        given(redisService.getUser(loginRequest.getUsername())).willReturn(userRedis);
        given(passwordEncoder.matches(loginRequest.getPassword(), userRedis.getPassword())).willReturn(false);

        try {
            authService.login(loginRequest);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(redisService, never()).saveToken(any(), any());
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        given(jwtService.extractUsername(refreshTokenRequest.getToken())).willReturn(userRedis.getUsername());
        given(redisService.getUser(userRedis.getUsername())).willReturn(userRedis);
        given(jwtService.isTokenValid(anyString(), any(User.class))).willReturn(true); // Use any() for relaxed matching
        given(jwtService.generateToken(any(User.class))).willReturn("newTestToken");

        JwtResponse jwtResponse = authService.refreshToken(refreshTokenRequest);

        assertThat(jwtResponse.getToken()).isEqualTo("newTestToken");
        assertThat(jwtResponse.getRefreshToken()).isEqualTo(refreshTokenRequest.getToken());

        verify(jwtService).generateToken(any(User.class)); // Use any() for verification
    }

    @Test
    void shouldFailRefreshTokenWithInvalidToken() {
        given(jwtService.extractUsername(refreshTokenRequest.getToken())).willReturn(userRedis.getUsername());
        given(redisService.getUser(userRedis.getUsername())).willReturn(userRedis);
        given(jwtService.isTokenValid(anyString(), any(User.class))).willReturn(false); // Use any() for relaxed
                                                                                        // matching

        try {
            authService.refreshToken(refreshTokenRequest);
        } catch (ResponseStatusException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        verify(jwtService, never()).generateToken(any(User.class)); // Use any() for verification
    }
}
