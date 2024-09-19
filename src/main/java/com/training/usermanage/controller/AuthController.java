package com.training.usermanage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.training.usermanage.model.User;
import com.training.usermanage.model.UserRedis;
import com.training.usermanage.request.LoginRequest;
import com.training.usermanage.request.RefreshTokenRequest;
import com.training.usermanage.request.RegisterRequest;
import com.training.usermanage.request.TokenRequest;
import com.training.usermanage.response.JwtResponse;
import com.training.usermanage.service.implement.AuthServiceImplement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "Operations to manage users authentication")
public class AuthController {

    @Autowired
    private AuthServiceImplement authService;

    @Operation(summary = "Register user and save to Redis")
    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<UserRedis> register(@RequestBody RegisterRequest registerRequest) {
        UserRedis registeredUser = authService.register(registerRequest);

        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

    @Operation(summary = "Generate new token when token is expired")
    @PostMapping("/refreshToken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    })
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        JwtResponse jwtResponse = authService.refreshToken(refreshTokenRequest);

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @Operation(summary = "User login")
    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User log in successfully", content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    })
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }

    @Operation(summary = "Validate token for Asteroid Service")
    @PostMapping("/validate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public ResponseEntity<Boolean> refresh(@RequestBody TokenRequest tokenRequest) {
        boolean status = authService.validate(tokenRequest);

        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
