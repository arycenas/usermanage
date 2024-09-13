package com.training.usermanage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.training.usermanage.model.Role;
import com.training.usermanage.model.UserRedis;
import com.training.usermanage.service.implement.RedisServiceImplement;

@ExtendWith(MockitoExtension.class)
class RedisServiceImplementTest {

    @Mock
    private RedisTemplate<String, UserRedis> redisTemplate;

    @Mock
    private ValueOperations<String, UserRedis> valueOperations;

    @Mock
    private HashOperations<String, String, String> hashOperations;

    @InjectMocks
    private RedisServiceImplement redisServiceImplement;

    private UserRedis userRedis;

    @BeforeEach
    void setUp() {
        userRedis = new UserRedis();
        userRedis.setUsername("testuser");
        userRedis.setPassword("password");
        userRedis.setRole(Role.USER);

        // Mock opsForValue and opsForHash
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    void shouldSaveUserToRedis() {
        redisServiceImplement.saveUser("testuser", userRedis);

        verify(valueOperations).set("testuser", userRedis);
    }

    @Test
    void shouldReturnUserFromRedis() {
        given(valueOperations.get("testuser")).willReturn(userRedis);

        UserRedis foundUserRedis = redisServiceImplement.getUser("testuser");

        assertThat(foundUserRedis).isNotNull();
        assertThat(foundUserRedis.getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldSaveTokenForUserInRedis() {
        given(valueOperations.get("testuser")).willReturn(userRedis);

        redisServiceImplement.saveToken("token", "testuser");

        verify(valueOperations).set("testuser", userRedis);
        assertThat(userRedis.getToken()).isEqualTo("token");
    }
}
