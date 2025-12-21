package com.isthereanyone.backend.dto;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String displayName;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String username, String email, String displayName, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

