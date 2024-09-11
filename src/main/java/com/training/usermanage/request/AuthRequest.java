package com.training.usermanage.request;

import lombok.Data;

@Data
public class AuthRequest {

    private String username;
    private String password;
}
