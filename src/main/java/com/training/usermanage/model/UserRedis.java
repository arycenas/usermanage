package com.training.usermanage.model;

public class UserRedis {

    private String username;
    private String password;
    private String token;
    private Role role;

    public UserRedis() {
    }

    public UserRedis(String username, String password, String token, Role role) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.role = role;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
