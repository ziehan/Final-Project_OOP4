package com.isthereanyone.frontend.network.dto;

/**
 * Response DTO untuk authentication (login/signup)
 */
public class AuthResponse {
    private String message;
    private UserResponse user;
    private String token;

    public AuthResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

