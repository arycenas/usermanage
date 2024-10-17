package com.training.usermanage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.training.usermanage.model.UserRedis;

@Service
public class RedisService {

    private static final Logger log = LoggerFactory.getLogger(RedisService.class);
    private final RedisTemplate<String, UserRedis> redisTemplate;

    public RedisService(RedisTemplate<String, UserRedis> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveUser(String userId, UserRedis userRedis) {
        redisTemplate.opsForValue().set(userId, userRedis);
        log.info("User with ID: {} saved successfully", userId);
    }

    public UserRedis getUser(String userId) {
        UserRedis userRedis = redisTemplate.opsForValue().get(userId);
        if (userRedis == null) {
            log.warn("User with ID: {} not found in Redis", userId);
        } else {
            log.info("User with ID: {} retrieved successfully", userId);
        }

        return userRedis;
    }

    public void saveToken(String token, String userId) {
        UserRedis userRedis = redisTemplate.opsForValue().get(userId);
        if (userRedis == null) {
            log.warn("User with ID: {} not found, token not saved", userId);
        } else {
            userRedis.setToken(token);
            redisTemplate.opsForValue().set(userId, userRedis);
            log.info("Token saved successfully for user with ID: {}", userId);
        }
    }
}
