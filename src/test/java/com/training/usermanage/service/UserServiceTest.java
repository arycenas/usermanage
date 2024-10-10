package com.training.usermanage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.training.usermanage.model.Role;
import com.training.usermanage.model.User;
import com.training.usermanage.model.UserRedis;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private RedisTemplate<String, UserRedis> redisTemplate;

    @Mock
    private ValueOperations<String, UserRedis> valueOperations;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRedis userRedis;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        userRedis = new UserRedis();
        userRedis.setUsername(user.getUsername());
        userRedis.setPassword(user.getPassword());
        userRedis.setRole(Role.USER);

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
    }

    @Test
    void shouldSaveUserToRedis() {
        ArgumentCaptor<UserRedis> captor = ArgumentCaptor.forClass(UserRedis.class);

        userService.saveUser(user);

        verify(valueOperations).set(eq("testuser"), captor.capture());

        UserRedis capturedUserRedis = captor.getValue();
        assertThat(capturedUserRedis.getUsername()).isEqualTo(userRedis.getUsername());
        assertThat(capturedUserRedis.getPassword()).isEqualTo(userRedis.getPassword());
        assertThat(capturedUserRedis.getRole()).isEqualTo(userRedis.getRole());
    }

    @Test
    void shouldReturnUserWhenFoundInRedis() {
        given(valueOperations.get("testuser")).willReturn(userRedis);

        User foundUser = userService.findUserByUsername("testuser");

        assertThat(foundUser.getUsername()).isEqualTo("testuser");
        assertThat(foundUser.getPassword()).isEqualTo("password");
        assertThat(foundUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInRedis() {
        given(valueOperations.get("unknownuser")).willReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.findUserByUsername("unknownuser"));
    }
}
