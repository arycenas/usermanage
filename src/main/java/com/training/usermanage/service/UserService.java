package com.training.usermanage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.training.usermanage.model.User;
import com.training.usermanage.model.UserRedis;

@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final RedisTemplate<String, UserRedis> redisTemplate;

    public UserService(RedisTemplate<String, UserRedis> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByUsername(username);
    }

    public void saveUser(User user) {
        UserRedis userRedis = new UserRedis();
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(user.getRole());

        redisTemplate.opsForValue().set(user.getUsername(), userRedis);
        log.info("User {} saved successfully to Redis", user.getUsername());
    }

    public User findUserByUsername(String username) {
        UserRedis userRedis = redisTemplate.opsForValue().get(username);
        if (userRedis == null) {
            log.error("User not found in Redis: {}", username);
            throw new UsernameNotFoundException("User not found");
        }

        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        log.info("User {} found in Redis", username);
        return user;
    }
}
