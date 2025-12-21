package com.isthereanyone.api;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

/**
 * API Service untuk berkomunikasi dengan backend
 * Gunakan class ini untuk semua operasi dengan server
 *
 * Contoh penggunaan:
 * <pre>
 * ApiService api = new ApiService();
 *
 * // Login
 * api.signin("username", "password", new ApiService.ApiCallback() {
 *     @Override
 *     public void onSuccess(JsonValue response) {
 *         JsonValue user = response.get("data").get("user");
 *         String username = user.getString("username");
 *     }
 *
 *     @Override
 *     public void onError(String error) {
 *         System.out.println("Error: " + error);
 *     }
 * });
 * </pre>
 */
public class ApiService {

    private static final String BASE_URL = "http://localhost:9090/api";
    private final Json json;

    public ApiService() {
        this.json = new Json();
        this.json.setOutputType(JsonWriter.OutputType.json);
    }

    // ==================== CALLBACK INTERFACE ====================

    /**
     * Interface untuk menangani response API
     */
    public interface ApiCallback {
        void onSuccess(JsonValue response);
        void onError(String error);
    }

    // ==================== HELPER METHODS ====================

    private void sendGetRequest(String endpoint, ApiCallback callback) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder
                .newRequest()
                .method(Net.HttpMethods.GET)
                .url(BASE_URL + endpoint)
                .header("Content-Type", "application/json")
                .build();

        sendRequest(request, callback);
    }

    private void sendPostRequest(String endpoint, String body, ApiCallback callback) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder
                .newRequest()
                .method(Net.HttpMethods.POST)
                .url(BASE_URL + endpoint)
                .header("Content-Type", "application/json")
                .content(body)
                .build();

        sendRequest(request, callback);
    }

    private void sendDeleteRequest(String endpoint, ApiCallback callback) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder
                .newRequest()
                .method(Net.HttpMethods.DELETE)
                .url(BASE_URL + endpoint)
                .header("Content-Type", "application/json")
                .build();

        sendRequest(request, callback);
    }

    private void sendRequest(Net.HttpRequest request, ApiCallback callback) {
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String responseString = httpResponse.getResultAsString();
                try {
                    JsonValue jsonResponse = new Json().fromJson(null, responseString);

                    boolean success = jsonResponse.getBoolean("success", false);
                    if (success) {
                        // Jalankan di main thread (GL thread)
                        Gdx.app.postRunnable(() -> callback.onSuccess(jsonResponse));
                    } else {
                        String message = jsonResponse.getString("message", "Unknown error");
                        Gdx.app.postRunnable(() -> callback.onError(message));
                    }
                } catch (Exception e) {
                    Gdx.app.postRunnable(() -> callback.onError("Failed to parse response: " + e.getMessage()));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError("Request failed: " + t.getMessage()));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError("Request cancelled"));
            }
        });
    }

    // ==================== AUTH API ====================

    /**
     * Registrasi user baru
     *
     * @param username Username (3-50 karakter)
     * @param email Email valid
     * @param password Password (minimal 6 karakter)
     * @param displayName Display name (opsional, bisa null)
     * @param callback Callback untuk response
     */
    public void signup(String username, String email, String password, String displayName, ApiCallback callback) {
        StringBuilder body = new StringBuilder();
        body.append("{");
        body.append("\"username\":\"").append(escapeJson(username)).append("\",");
        body.append("\"email\":\"").append(escapeJson(email)).append("\",");
        body.append("\"password\":\"").append(escapeJson(password)).append("\"");
        if (displayName != null && !displayName.isEmpty()) {
            body.append(",\"displayName\":\"").append(escapeJson(displayName)).append("\"");
        }
        body.append("}");

        sendPostRequest("/auth/signup", body.toString(), callback);
    }

    /**
     * Login user
     *
     * @param usernameOrEmail Username atau email
     * @param password Password
     * @param callback Callback untuk response
     */
    public void signin(String usernameOrEmail, String password, ApiCallback callback) {
        String body = "{" +
                "\"usernameOrEmail\":\"" + escapeJson(usernameOrEmail) + "\"," +
                "\"password\":\"" + escapeJson(password) + "\"" +
                "}";

        sendPostRequest("/auth/signin", body, callback);
    }

    /**
     * Cek apakah username sudah digunakan
     *
     * @param username Username yang dicek
     * @param callback Callback, data berisi boolean (true = sudah digunakan)
     */
    public void checkUsername(String username, ApiCallback callback) {
        sendGetRequest("/auth/check/username/" + username, callback);
    }

    /**
     * Cek apakah email sudah terdaftar
     *
     * @param email Email yang dicek
     * @param callback Callback, data berisi boolean (true = sudah terdaftar)
     */
    public void checkEmail(String email, ApiCallback callback) {
        sendGetRequest("/auth/check/email/" + encodeUrl(email), callback);
    }

    /**
     * Get user by username
     *
     * @param username Username
     * @param callback Callback dengan data user
     */
    public void getUser(String username, ApiCallback callback) {
        sendGetRequest("/auth/user/" + username, callback);
    }

    // ==================== GAME SAVE API ====================

    /**
     * Simpan game ke slot tertentu
     *
     * @param userId User ID (bisa username)
     * @param slotId Slot ID (1-3)
     * @param saveData Data save dalam format JSON string
     * @param callback Callback untuk response
     */
    public void saveGame(String userId, int slotId, String saveData, ApiCallback callback) {
        String body = "{" +
                "\"userId\":\"" + escapeJson(userId) + "\"," +
                "\"slotId\":" + slotId + "," +
                "\"saveData\":" + saveData +
                "}";

        sendPostRequest("/save", body, callback);
    }

    /**
     * Load game dari slot tertentu
     *
     * @param userId User ID
     * @param slotId Slot ID (1-3)
     * @param callback Callback dengan data save
     */
    public void loadGame(String userId, int slotId, ApiCallback callback) {
        sendGetRequest("/save/" + userId + "/" + slotId, callback);
    }

    /**
     * Get semua slot info (untuk tampilan menu load game)
     *
     * @param userId User ID
     * @param callback Callback dengan array slot info
     */
    public void getAllSlots(String userId, ApiCallback callback) {
        sendGetRequest("/save/" + userId + "/slots", callback);
    }

    /**
     * Hapus slot tertentu
     *
     * @param userId User ID
     * @param slotId Slot ID (1-3)
     * @param callback Callback untuk response
     */
    public void deleteSlot(String userId, int slotId, ApiCallback callback) {
        sendDeleteRequest("/save/" + userId + "/" + slotId, callback);
    }

    /**
     * Hapus semua slot user
     *
     * @param userId User ID
     * @param callback Callback untuk response
     */
    public void deleteAllSlots(String userId, ApiCallback callback) {
        sendDeleteRequest("/save/" + userId, callback);
    }

    /**
     * Cek apakah slot sudah ada isinya
     *
     * @param userId User ID
     * @param slotId Slot ID
     * @param callback Callback, data berisi boolean
     */
    public void checkSlotExists(String userId, int slotId, ApiCallback callback) {
        sendGetRequest("/save/" + userId + "/" + slotId + "/exists", callback);
    }

    // ==================== HEALTH API ====================

    /**
     * Health check - untuk memastikan server berjalan
     *
     * @param callback Callback dengan status server
     */
    public void healthCheck(ApiCallback callback) {
        sendGetRequest("/health", callback);
    }

    /**
     * Simple ping
     *
     * @param callback Callback dengan "pong"
     */
    public void ping(ApiCallback callback) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder
                .newRequest()
                .method(Net.HttpMethods.GET)
                .url(BASE_URL + "/ping")
                .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();
                Gdx.app.postRunnable(() -> {
                    JsonValue json = new JsonValue(result);
                    callback.onSuccess(json);
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onError("Ping failed: " + t.getMessage()));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onError("Ping cancelled"));
            }
        });
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Escape special characters untuk JSON string
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Encode URL (simple version untuk email)
     */
    private String encodeUrl(String text) {
        if (text == null) return "";
        return text.replace("@", "%40");
    }
}

