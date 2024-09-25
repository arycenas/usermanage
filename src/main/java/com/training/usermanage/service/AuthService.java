package com.training.usermanage.service;

import com.training.usermanage.model.UserRedis;
import com.training.usermanage.request.TokenRequest;
import com.training.usermanage.request.UserRequest;
import com.training.usermanage.response.JwtResponse;

public interface AuthService {

    UserRedis register(UserRequest registerRequest);

    JwtResponse login(UserRequest loginRequest);

    JwtResponse refreshToken(TokenRequest refreshTokenRequest);

    boolean validate(TokenRequest tokenRequest);
}
