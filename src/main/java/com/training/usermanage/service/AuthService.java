package com.training.usermanage.service;

import com.training.usermanage.model.User;
import com.training.usermanage.request.LoginRequest;
import com.training.usermanage.request.RefreshTokenRequest;
import com.training.usermanage.request.RegisterRequest;
import com.training.usermanage.response.JwtResponse;

public interface AuthService {

    User register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
