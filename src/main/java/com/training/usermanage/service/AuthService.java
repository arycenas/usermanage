package com.training.usermanage.service;

import com.training.usermanage.model.UserRedis;
import com.training.usermanage.request.LoginRequest;
import com.training.usermanage.request.RefreshTokenRequest;
import com.training.usermanage.request.RegisterRequest;
import com.training.usermanage.request.TokenRequest;
import com.training.usermanage.response.JwtResponse;

public interface AuthService {

    UserRedis register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);

    JwtResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

    boolean validate(TokenRequest tokenRequest);
}
