package com.training.usermanage.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.training.usermanage.model.User;

public interface UserService extends UserDetailsService {

    void saveUser(User user);

    User findUserByUsername(String username);
}
