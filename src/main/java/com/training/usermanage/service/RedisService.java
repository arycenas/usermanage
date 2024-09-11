package com.training.usermanage.service;

import com.training.usermanage.model.User;

public interface RedisService {

    void saveUser(String userId, User user);

    User getUser(String userId);

    void saveToken(String token, String userId);

    String getUserIdFromToken(String token);
}
