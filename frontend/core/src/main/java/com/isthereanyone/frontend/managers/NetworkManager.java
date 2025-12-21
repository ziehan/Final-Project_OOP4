package com.isthereanyone.frontend.managers;

import com.isthereanyone.frontend.network.ApiService;
import com.isthereanyone.frontend.network.NetworkCallback;
import com.isthereanyone.frontend.network.dto.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NetworkManager - Manages all network communications with backend
 * Provides both sync and async methods for flexibility
 */
public class NetworkManager {
    private static NetworkManager instance;
    private final ApiService apiService;

    // Session data
    private UserResponse currentUser;
    private String authToken;
    private boolean isLoggedIn;

    private NetworkManager() {
        this.apiService = ApiService.getInstance();
        this.isLoggedIn = false;
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public void login(String usernameOrEmail, String password, NetworkCallback<ApiResponse<AuthResponse>> callback) {
        SigninRequest request = new SigninRequest(usernameOrEmail, password);
        apiService.signin(request, new NetworkCallback<ApiResponse<AuthResponse>>() {
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

    public void signup(String username, String email, String password, String displayName,
                       NetworkCallback<ApiResponse<AuthResponse>> callback) {
        SignupRequest request = new SignupRequest(username, email, password, displayName);
        apiService.signup(request, new NetworkCallback<ApiResponse<AuthResponse>>() {
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
        this.currentUser = null;
        this.authToken = null;
        this.isLoggedIn = false;
        System.out.println("[NetworkManager] User logged out");
    }

    public void saveGame(int slotId, Map<String, Object> saveData,
                         NetworkCallback<ApiResponse<GameSaveResponse>> callback) {
        if (!isLoggedIn || currentUser == null) {
            callback.onFailure("User not logged in");
            return;
        }

        SaveGameRequest request = new SaveGameRequest(
            String.valueOf(currentUser.getId()),
            slotId,
            saveData
        );
        apiService.saveGame(request, callback);
    }

    public void loadGame(int slotId, NetworkCallback<ApiResponse<GameSaveResponse>> callback) {
        if (!isLoggedIn || currentUser == null) {
            callback.onFailure("User not logged in");
            return;
        }

        apiService.loadGame(String.valueOf(currentUser.getId()), slotId, callback);
    }

    public void getAllSlots(NetworkCallback<ApiResponse<List<SlotInfo>>> callback) {
        if (!isLoggedIn || currentUser == null) {
            callback.onFailure("User not logged in");
            return;
        }

        apiService.getAllSlots(String.valueOf(currentUser.getId()), callback);
    }

    public void deleteSlot(int slotId, NetworkCallback<ApiResponse<Void>> callback) {
        if (!isLoggedIn || currentUser == null) {
            callback.onFailure("User not logged in");
            return;
        }

        apiService.deleteSlot(String.valueOf(currentUser.getId()), slotId, callback);
    }

    // ==================== HEALTH CHECK ====================

    public void ping(NetworkCallback<String> callback) {
        apiService.ping(callback);
    }

    public void healthCheck(NetworkCallback<ApiResponse<Map<String, Object>>> callback) {
        apiService.healthCheck(callback);
    }

    // ==================== GETTERS ====================

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public UserResponse getCurrentUser() {
        return currentUser;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getCurrentUserId() {
        return currentUser != null ? String.valueOf(currentUser.getId()) : null;
    }

    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public String getCurrentDisplayName() {
        return currentUser != null ? currentUser.getDisplayName() : null;
    }

    // ==================== HELPER METHODS ====================

    private void handleAuthSuccess(AuthResponse authResponse) {
        this.currentUser = authResponse.getUser();
        this.authToken = authResponse.getToken();
        this.isLoggedIn = true;
        System.out.println("[NetworkManager] User logged in: " + currentUser.getUsername());
    }

    // ==================== LEGACY SYNC METHODS (for backward compatibility) ====================

    /**
     * @deprecated Use async login() with callback instead
     */
    @Deprecated
    public boolean login(String username, String password) {
        System.out.println("[LOGIN] " + username + " (sync method - use async for real backend)");
        return true;
    }

    /**
     * @deprecated Use async signup() with callback instead
     */
    @Deprecated
    public boolean signup(String username, String password) {
        System.out.println("[SIGNUP] " + username + " (sync method - use async for real backend)");
        return true;
    }

    /**
     * @deprecated Use async getAllSlots() with callback instead
     */
    @Deprecated
    public boolean loadSaveSlots(String username) {
        System.out.println("[LOAD SLOTS] " + username + " (sync method - use async for real backend)");
        return true;
    }

    /**
     * @deprecated Use async saveGame() with callback instead
     */
    @Deprecated
    public boolean saveGameData(String username, int slotNumber, SaveSlotManager.SaveSlotData saveData) {
        System.out.println("[SAVE] Slot " + slotNumber + " (sync method - use async for real backend)");
        return true;
    }
}
