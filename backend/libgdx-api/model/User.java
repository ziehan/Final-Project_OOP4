package com.isthereanyone.api.model;

/**
 * Model class untuk data User
 */
public class User {
    private long id;
    private String username;
    private String email;
    private String displayName;
    private String createdAt;

    public User() {}

    public User(long id, String username, String email, String displayName, String createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }
}

