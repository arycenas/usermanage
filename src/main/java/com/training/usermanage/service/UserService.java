package com.training.usermanage.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.training.usermanage.model.User;
import com.training.usermanage.model.UserRedis;

@Service
public class UserService implements UserDetailsService {

    private final RedisTemplate<String, UserRedis> redisTemplate;

    public UserService(RedisTemplate<String, UserRedis> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveUser(User user) {
        UserRedis userRedis = new UserRedis();
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(user.getRole());

        redisTemplate.opsForValue().set(user.getUsername(), userRedis);
    }

    public User findUserByUsername(String username) {
        UserRedis userRedis = redisTemplate.opsForValue().get(username);
        if (userRedis == null) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = new User();
        user.setUsername(userRedis.getUsername());
        user.setPassword(userRedis.getPassword());
        user.setRole(userRedis.getRole());

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByUsername(username);
    }
}
