package com.training.usermanage.service.implement;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.training.usermanage.model.UserRedis;
import com.training.usermanage.service.RedisService;

@Service
public class RedisServiceImplement implements RedisService {

    private final RedisTemplate<String, UserRedis> redisTemplate;

    public RedisServiceImplement(RedisTemplate<String, UserRedis> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveUser(String userId, UserRedis userRedis) {
        redisTemplate.opsForValue().set(userId, userRedis);
    }

    @Override
    public UserRedis getUser(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    @Override
    public void saveToken(String token, String userId) {
        UserRedis userRedis = redisTemplate.opsForValue().get(userId);
        userRedis.setToken(token);
        redisTemplate.opsForValue().set(userId, userRedis);
    }
}
