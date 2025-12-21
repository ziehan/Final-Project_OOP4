package com.isthereanyone.frontend.network;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.network.dto.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Service untuk melakukan HTTP request ke backend API
 */
public class ApiService {
    private static ApiService instance;
    private final Gson gson;
    private final String baseUrl;

    private static final int CONNECT_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000; // 30 seconds

    private ApiService() {
        this.gson = new GsonBuilder().create();
        this.baseUrl = GameConfig.API_BASE_URL;
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    // ==================== AUTH ENDPOINTS ====================

    /**
     * Register user baru
     */
    public void signup(SignupRequest request, NetworkCallback<ApiResponse<AuthResponse>> callback) {
        executeAsync(() -> {
            try {
                String json = gson.toJson(request);
                String response = post("/auth/signup", json);
                Type type = new TypeToken<ApiResponse<AuthResponse>>(){}.getType();
                ApiResponse<AuthResponse> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Login user
     */
    public void signin(SigninRequest request, NetworkCallback<ApiResponse<AuthResponse>> callback) {
        executeAsync(() -> {
            try {
                String json = gson.toJson(request);
                String response = post("/auth/signin", json);
                Type type = new TypeToken<ApiResponse<AuthResponse>>(){}.getType();
                ApiResponse<AuthResponse> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Cek apakah username tersedia
     */
    public void checkUsername(String username, NetworkCallback<ApiResponse<Boolean>> callback) {
        executeAsync(() -> {
            try {
                String response = get("/auth/check/username/" + username);
                Type type = new TypeToken<ApiResponse<Boolean>>(){}.getType();
                ApiResponse<Boolean> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Cek apakah email tersedia
     */
    public void checkEmail(String email, NetworkCallback<ApiResponse<Boolean>> callback) {
        executeAsync(() -> {
            try {
                String response = get("/auth/check/email/" + email);
                Type type = new TypeToken<ApiResponse<Boolean>>(){}.getType();
                ApiResponse<Boolean> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Get user info by username
     */
    public void getUserByUsername(String username, NetworkCallback<ApiResponse<UserResponse>> callback) {
        executeAsync(() -> {
            try {
                String response = get("/auth/user/" + username);
                Type type = new TypeToken<ApiResponse<UserResponse>>(){}.getType();
                ApiResponse<UserResponse> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    // ==================== GAME SAVE ENDPOINTS ====================

    /**
     * Save game ke slot tertentu
     */
    public void saveGame(SaveGameRequest request, NetworkCallback<ApiResponse<GameSaveResponse>> callback) {
        executeAsync(() -> {
            try {
                String json = gson.toJson(request);
                String response = post("/save", json);
                Type type = new TypeToken<ApiResponse<GameSaveResponse>>(){}.getType();
                ApiResponse<GameSaveResponse> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Load game dari slot tertentu
     */
    public void loadGame(String oderId, int slotId, NetworkCallback<ApiResponse<GameSaveResponse>> callback) {
        executeAsync(() -> {
            try {
                String response = get("/save/" + oderId + "/" + slotId);
                Type type = new TypeToken<ApiResponse<GameSaveResponse>>(){}.getType();
                ApiResponse<GameSaveResponse> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Get semua slot info untuk user
     */
    public void getAllSlots(String userId, NetworkCallback<ApiResponse<List<SlotInfo>>> callback) {
        executeAsync(() -> {
            try {
                String response = get("/save/" + userId + "/slots");
                Type type = new TypeToken<ApiResponse<List<SlotInfo>>>(){}.getType();
                ApiResponse<List<SlotInfo>> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Hapus save slot tertentu
     */
    public void deleteSlot(String userId, int slotId, NetworkCallback<ApiResponse<Void>> callback) {
        executeAsync(() -> {
            try {
                String response = delete("/save/" + userId + "/" + slotId);
                Type type = new TypeToken<ApiResponse<Void>>(){}.getType();
                ApiResponse<Void> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Hapus semua save slot user
     */
    public void deleteAllSlots(String userId, NetworkCallback<ApiResponse<Void>> callback) {
        executeAsync(() -> {
            try {
                String response = delete("/save/" + userId);
                Type type = new TypeToken<ApiResponse<Void>>(){}.getType();
                ApiResponse<Void> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Cek apakah slot exists
     */
    public void checkSlotExists(String userId, int slotId, NetworkCallback<ApiResponse<Boolean>> callback) {
        executeAsync(() -> {
            try {
                String response = get("/save/" + userId + "/" + slotId + "/exists");
                Type type = new TypeToken<ApiResponse<Boolean>>(){}.getType();
                ApiResponse<Boolean> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    // ==================== HEALTH CHECK ====================

    /**
     * Health check ke server
     */
    public void healthCheck(NetworkCallback<ApiResponse<Map<String, Object>>> callback) {
        executeAsync(() -> {
            try {
                String response = get("/health");
                Type type = new TypeToken<ApiResponse<Map<String, Object>>>(){}.getType();
                ApiResponse<Map<String, Object>> result = gson.fromJson(response, type);
                Gdx.app.postRunnable(() -> callback.onSuccess(result));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    /**
     * Ping server
     */
    public void ping(NetworkCallback<String> callback) {
        executeAsync(() -> {
            try {
                String response = get("/ping");
                Gdx.app.postRunnable(() -> callback.onSuccess(response));
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    // ==================== HTTP METHODS ====================

    private String get(String endpoint) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        return readResponse(conn);
    }

    private String post(String endpoint, String jsonBody) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return readResponse(conn);
    }

    private String delete(String endpoint) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        InputStream is;

        if (responseCode >= 200 && responseCode < 300) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
        }

        if (is == null) {
            throw new IOException("No response from server");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private void executeAsync(Runnable task) {
        new Thread(task).start();
    }
}

