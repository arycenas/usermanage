package com.training.usermanage.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.training.usermanage.model.UserRedis;

@Service
public class RedisService {

    private final RedisTemplate<String, UserRedis> redisTemplate;

    public RedisService(RedisTemplate<String, UserRedis> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveUser(String userId, UserRedis userRedis) {
        redisTemplate.opsForValue().set(userId, userRedis);
    }

    public UserRedis getUser(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    public void saveToken(String token, String userId) {
        UserRedis userRedis = redisTemplate.opsForValue().get(userId);
        userRedis.setToken(token);
        redisTemplate.opsForValue().set(userId, userRedis);
    }
}
