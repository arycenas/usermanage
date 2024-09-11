package com.training.usermanage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.training.usermanage.model.User;
import com.training.usermanage.request.LoginRequest;
import com.training.usermanage.request.RefreshTokenRequest;
import com.training.usermanage.request.RegisterRequest;
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

    private final AuthServiceImplement authService;

    @Operation(summary = "Register user and save to Redis")
    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = User.class)))
    })
    public ResponseEntity<User> register(@RequestBody RegisterRequest registerRequest) {
        System.out.println("RegisterRequest: " + registerRequest);
        User registeredUser = authService.register(registerRequest);

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

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.login(loginRequest);

        return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
    }
}
