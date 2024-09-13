package com.training.usermanage.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.usermanage.component.UserSerializer;
import com.training.usermanage.model.UserRedis;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserRedis> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, UserRedis> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new UserSerializer(new ObjectMapper()));

        return redisTemplate;
    }
}
