package com.isthereanyone.frontend.managers;

import com.isthereanyone.frontend.network.NetworkCallback;
import com.isthereanyone.frontend.network.dto.ApiResponse;
import com.isthereanyone.frontend.network.dto.AuthResponse;
import com.isthereanyone.frontend.network.dto.UserResponse;

/**
 * AuthenticationManager - Manages user authentication state
 * Now connected to backend via NetworkManager
 */
public class AuthenticationManager {
    private static AuthenticationManager instance;
    private String currentUsername;
    private String currentToken;
    private Long currentUserId;
    private boolean isAuthenticated = false;

    private AuthenticationManager() {}

    public static AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }

    public void signup(String username, String email, String password, String displayName,
                       NetworkCallback<ApiResponse<AuthResponse>> callback) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            callback.onFailure("Username dan password tidak boleh kosong");
            return;
        }

        NetworkManager.getInstance().signup(username, email, password, displayName,
            new NetworkCallback<ApiResponse<AuthResponse>>() {
                @Override
                public void onSuccess(ApiResponse<AuthResponse> result) {
                    if (result.isSuccess() && result.getData() != null) {
                        handleAuthSuccess(result.getData());
                    }
                    callback.onSuccess(result);
                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure(error);
                }
            });
    }

    public void login(String usernameOrEmail, String password,
                      NetworkCallback<ApiResponse<AuthResponse>> callback) {
        if (usernameOrEmail == null || usernameOrEmail.isEmpty() || password == null || password.isEmpty()) {
            callback.onFailure("Username/email dan password tidak boleh kosong");
            return;
        }

        NetworkManager.getInstance().login(usernameOrEmail, password,
            new NetworkCallback<ApiResponse<AuthResponse>>() {
                @Override
                public void onSuccess(ApiResponse<AuthResponse> result) {
                    if (result.isSuccess() && result.getData() != null) {
                        handleAuthSuccess(result.getData());
                    }
                    callback.onSuccess(result);
                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure(error);
                }
            });
    }

    public void logout() {
        currentUsername = null;
        currentToken = null;
        currentUserId = null;
        isAuthenticated = false;
        NetworkManager.getInstance().logout();
        System.out.println("[AUTH] User logged out");
    }

    private void handleAuthSuccess(AuthResponse authResponse) {
        UserResponse user = authResponse.getUser();
        if (user != null) {
            this.currentUsername = user.getUsername();
            this.currentUserId = user.getId();
        }
        this.currentToken = authResponse.getToken();
        this.isAuthenticated = true;
        System.out.println("[AUTH] Authentication successful for: " + currentUsername);
    }

    // ==================== GETTERS ====================

    public String getCurrentUsername() {
        // Prioritas dari NetworkManager jika sudah login via async
        String networkUsername = NetworkManager.getInstance().getCurrentUsername();
        return networkUsername != null ? networkUsername : currentUsername;
    }

    public String getCurrentToken() {
        return currentToken;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public boolean isAuthenticated() {
        return isAuthenticated || NetworkManager.getInstance().isLoggedIn();
    }

    // ==================== LEGACY SYNC METHODS (for backward compatibility) ====================

    /**
     * @deprecated Use async login() with callback instead
     */
    @Deprecated
    public boolean login(String username, String password) {
        System.out.println("[AUTH] Sync login called - use async method for real backend");
        // Return true for backward compatibility with existing screens
        currentUsername = username;
        isAuthenticated = true;
        return true;
    }

    /**
     * @deprecated Use async signup() with callback instead
     */
    @Deprecated
    public boolean signup(String username, String password) {
        System.out.println("[AUTH] Sync signup called - use async method for real backend");
        // Return true for backward compatibility
        return true;
    }
}
