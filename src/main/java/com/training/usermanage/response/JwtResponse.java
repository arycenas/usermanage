package com.training.usermanage.response;

import lombok.Data;

@Data
public class JwtResponse {

    private String token;
    private String refreshToken;
}
