package com.training.usermanage.service;

import com.training.usermanage.model.UserRedis;

public interface RedisService {

    void saveUser(String userId, UserRedis userRedis);

    UserRedis getUser(String userId);

    void saveToken(String token, String userId);

    String getUserIdFromToken(String token);
}
