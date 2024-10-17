package com.training.usermanage.service;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.training.usermanage.model.Role;
import com.training.usermanage.model.User;
import com.training.usermanage.model.UserRedis;
import com.training.usermanage.request.TokenRequest;
import com.training.usermanage.request.UserRequest;
import com.training.usermanage.response.JwtResponse;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisService redisService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
            PasswordEncoder passwordEncoder, RedisService redisService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.redisService = redisService;
    }

    public UserRedis register(UserRequest registerRequest) {
        log.info("Registering user: {}", registerRequest.getUsername());

        UserRedis existingUser = redisService.getUser(registerRequest.getUsername());
        if (existingUser != null) {
            log.error("Username {} already exists", registerRequest.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        UserRedis userRedis = new UserRedis();
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(user.getRole());

        redisService.saveUser(user.getUsername(), userRedis);
        log.info("User {} registered successfully", user.getUsername());

        return userRedis;
    }

    public JwtResponse login(UserRequest loginRequest) {
        log.info("Logging in user: {}", loginRequest.getUsername());

        UserRedis userRedis = redisService.getUser(loginRequest.getUsername());
        if (userRedis == null) {
            log.error("Username not found: {}", loginRequest.getUsername());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found");
        }

        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.error("Invalid password for user: {}", loginRequest.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            log.info("Authentication successful for user: {}", loginRequest.getUsername());
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed");
        }

        var token = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        redisService.saveToken(token, user.getUsername());
        log.info("Token generated and saved for user: {}", loginRequest.getUsername());

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(token);
        jwtResponse.setRefreshToken(refreshToken);

        return jwtResponse;
    }

    public JwtResponse refreshToken(TokenRequest refreshTokenRequest) {
        log.info("Refreshing token for request...");

        String username = jwtService.extractUsername(refreshTokenRequest.getToken());

        UserRedis userRedis = redisService.getUser(username);
        if (userRedis == null) {
            log.error("Username not found");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            var token = jwtService.generateToken(user);
            log.info("New token generated for user: {}", username);

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(token);
            jwtResponse.setRefreshToken(refreshTokenRequest.getToken());

            return jwtResponse;
        }

        log.error("Invalid refresh token for user: {}", username);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    }

    public boolean validate(TokenRequest tokenRequest) {
        log.info("Validating token...");

        String username = jwtService.extractUsername(tokenRequest.getToken());

        UserRedis userRedis = redisService.getUser(username);
        if (userRedis == null) {
            log.error("Invalid token, user not found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        if (jwtService.isTokenValid(tokenRequest.getToken(), user)) {
            log.info("Token is valid for user: {}", username);
            return true;
        }

        log.error("Token is invalid for user: {}", username);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    }
}
