package com.isthereanyone.frontend.managers;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {
    private static AuthenticationManager instance;
    private String currentUsername;
    private String currentToken;
    private boolean isAuthenticated = false;
    private Map<String, String> localUsers = new HashMap<>();

    private AuthenticationManager() {
        localUsers.put("testuser", "password123");
    }

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    public boolean signup(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("[AUTH] Invalid username or password");
            return false;
        }

        if (localUsers.containsKey(username)) {
            System.out.println("[AUTH] Username already exists: " + username);
            return false;
        }

        if (NetworkManager.getInstance().signup(username, password)) {
            localUsers.put(username, password);
            System.out.println("[AUTH] Signup successful for: " + username);
            return true;
        }

        System.out.println("[AUTH] Signup failed for: " + username);
        return false;
    }

    public boolean login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            System.out.println("[AUTH] Invalid username or password");
            return false;
        }

        if (NetworkManager.getInstance().login(username, password)) {
            currentUsername = username;
            isAuthenticated = true;
            currentToken = generateToken();
            System.out.println("[AUTH] Login successful for: " + username);
            return true;
        }

        System.out.println("[AUTH] Login failed for: " + username);
        return false;
    }

    public void logout() {
        currentUsername = null;
        currentToken = null;
        isAuthenticated = false;
        System.out.println("[AUTH] User logged out");
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    private String generateToken() {
        return "token_" + System.currentTimeMillis() + "_" + currentUsername;
    }
}
