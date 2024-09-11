package com.training.usermanage.request;

import lombok.Data;

@Data
public class TokenRequest {

    private String username;
    private String token;
}
