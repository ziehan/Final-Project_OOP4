package com.isthereanyone.frontend.network;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.isthereanyone.frontend.network.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API Service - handles all backend API calls
 */
public class ApiService {
    private static ApiService instance;
    private final HttpClient httpClient;
    private final Json json;

    private ApiService() {
        httpClient = new HttpClient();
        json = new Json();
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    // ==================== AUTH API ====================

    public void signup(SignupRequest request, NetworkCallback<ApiResponse<AuthResponse>> callback) {
        httpClient.post("/api/auth/signup", request, new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<AuthResponse> response = parseAuthResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void signin(SigninRequest request, NetworkCallback<ApiResponse<AuthResponse>> callback) {
        httpClient.post("/api/auth/signin", request, new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<AuthResponse> response = parseAuthResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void getUser(String username, NetworkCallback<ApiResponse<UserResponse>> callback) {
        httpClient.get("/api/auth/user/" + username, new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<UserResponse> response = parseUserResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    // ==================== SAVE GAME API ====================

    public void saveGame(SaveGameRequest request, NetworkCallback<ApiResponse<GameSaveResponse>> callback) {
        httpClient.post("/api/save", request, new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<GameSaveResponse> response = parseGameSaveResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void loadGame(String userId, int slotId, NetworkCallback<ApiResponse<GameSaveResponse>> callback) {
        httpClient.get("/api/save/" + userId + "/" + slotId, new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<GameSaveResponse> response = parseGameSaveResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void getAllSlots(String userId, NetworkCallback<ApiResponse<List<SlotInfo>>> callback) {
        httpClient.get("/api/save/" + userId + "/slots", new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<List<SlotInfo>> response = parseSlotsResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void deleteSlot(String userId, int slotId, NetworkCallback<ApiResponse<Void>> callback) {
        httpClient.delete("/api/save/" + userId + "/" + slotId, new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<Void> response = parseVoidResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    // ==================== HEALTH API ====================

    public void ping(NetworkCallback<String> callback) {
        httpClient.get("/api/ping", callback);
    }

    public void healthCheck(NetworkCallback<ApiResponse<Map<String, Object>>> callback) {
        httpClient.get("/api/health", new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ApiResponse<Map<String, Object>> response = parseHealthResponse(result);
                    callback.onSuccess(response);
                } catch (Exception e) {
                    callback.onFailure("Failed to parse response: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    // ==================== PARSERS ====================

    private ApiResponse<AuthResponse> parseAuthResponse(String jsonString) {
        JsonValue root = new JsonReader().parse(jsonString);

        ApiResponse<AuthResponse> response = new ApiResponse<>();
        response.setSuccess(root.getBoolean("success"));
        response.setMessage(root.getString("message"));

        if (root.has("data") && !root.get("data").isNull()) {
            JsonValue dataJson = root.get("data");
            AuthResponse authResponse = new AuthResponse();

            if (dataJson.has("message")) {
                authResponse.setMessage(dataJson.getString("message"));
            }
            if (dataJson.has("token") && !dataJson.get("token").isNull()) {
                authResponse.setToken(dataJson.getString("token"));
            }

            if (dataJson.has("user") && !dataJson.get("user").isNull()) {
                JsonValue userJson = dataJson.get("user");
                UserResponse user = new UserResponse();
                user.setId(userJson.getLong("id"));
                user.setUsername(userJson.getString("username"));
                user.setEmail(userJson.getString("email"));
                if (userJson.has("displayName") && !userJson.get("displayName").isNull()) {
                    user.setDisplayName(userJson.getString("displayName"));
                }
                authResponse.setUser(user);
            }

            response.setData(authResponse);
        }

        return response;
    }

    private ApiResponse<UserResponse> parseUserResponse(String jsonString) {
        JsonValue root = new JsonReader().parse(jsonString);

        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setSuccess(root.getBoolean("success"));
        response.setMessage(root.getString("message"));

        if (root.has("data") && !root.get("data").isNull()) {
            JsonValue userJson = root.get("data");
            UserResponse user = new UserResponse();
            user.setId(userJson.getLong("id"));
            user.setUsername(userJson.getString("username"));
            user.setEmail(userJson.getString("email"));
            if (userJson.has("displayName") && !userJson.get("displayName").isNull()) {
                user.setDisplayName(userJson.getString("displayName"));
            }
            response.setData(user);
        }

        return response;
    }

    private ApiResponse<GameSaveResponse> parseGameSaveResponse(String jsonString) {
        JsonValue root = new JsonReader().parse(jsonString);

        ApiResponse<GameSaveResponse> response = new ApiResponse<>();
        response.setSuccess(root.getBoolean("success"));
        response.setMessage(root.getString("message"));

        if (root.has("data") && !root.get("data").isNull()) {
            JsonValue dataJson = root.get("data");
            GameSaveResponse saveResponse = new GameSaveResponse();
            saveResponse.setUserId(dataJson.getString("userId"));
            saveResponse.setSlotId(dataJson.getInt("slotId"));

            if (dataJson.has("lastUpdated") && !dataJson.get("lastUpdated").isNull()) {
                saveResponse.setLastUpdated(dataJson.getString("lastUpdated"));
            }

            if (dataJson.has("saveData") && !dataJson.get("saveData").isNull()) {
                Map<String, Object> saveData = parseJsonToMap(dataJson.get("saveData"));
                saveResponse.setSaveData(saveData);
            }

            response.setData(saveResponse);
        }

        return response;
    }

    private ApiResponse<List<SlotInfo>> parseSlotsResponse(String jsonString) {
        JsonValue root = new JsonReader().parse(jsonString);

        ApiResponse<List<SlotInfo>> response = new ApiResponse<>();
        response.setSuccess(root.getBoolean("success"));
        response.setMessage(root.getString("message"));

        List<SlotInfo> slots = new ArrayList<>();
        if (root.has("data") && !root.get("data").isNull()) {
            JsonValue dataArray = root.get("data");
            for (JsonValue slotJson : dataArray) {
                SlotInfo slot = new SlotInfo();
                slot.setSlotId(slotJson.getInt("slotId"));
                slot.setEmpty(slotJson.getBoolean("empty", true));

                if (slotJson.has("lastUpdated") && !slotJson.get("lastUpdated").isNull()) {
                    slot.setLastUpdated(slotJson.getString("lastUpdated"));
                }
                if (slotJson.has("currentMap") && !slotJson.get("currentMap").isNull()) {
                    slot.setCurrentMap(slotJson.getString("currentMap"));
                }
                if (slotJson.has("allTimeDeathCount") && !slotJson.get("allTimeDeathCount").isNull()) {
                    slot.setAllTimeDeathCount(slotJson.getInt("allTimeDeathCount"));
                }
                if (slotJson.has("allTimeCompletedTask") && !slotJson.get("allTimeCompletedTask").isNull()) {
                    slot.setAllTimeCompletedTask(slotJson.getInt("allTimeCompletedTask"));
                }

                slots.add(slot);
            }
        }
        response.setData(slots);

        return response;
    }

    private ApiResponse<Void> parseVoidResponse(String jsonString) {
        JsonValue root = new JsonReader().parse(jsonString);

        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(root.getBoolean("success"));
        response.setMessage(root.getString("message"));
        response.setData(null);

        return response;
    }

    private ApiResponse<Map<String, Object>> parseHealthResponse(String jsonString) {
        JsonValue root = new JsonReader().parse(jsonString);

        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        response.setSuccess(root.getBoolean("success"));
        response.setMessage(root.getString("message"));

        if (root.has("data") && !root.get("data").isNull()) {
            Map<String, Object> data = parseJsonToMap(root.get("data"));
            response.setData(data);
        }

        return response;
    }

    private Map<String, Object> parseJsonToMap(JsonValue jsonValue) {
        Map<String, Object> map = new HashMap<>();
        for (JsonValue entry = jsonValue.child; entry != null; entry = entry.next) {
            String key = entry.name;
            if (entry.isObject()) {
                map.put(key, parseJsonToMap(entry));
            } else if (entry.isArray()) {
                List<Object> list = new ArrayList<>();
                for (JsonValue item : entry) {
                    if (item.isObject()) {
                        list.add(parseJsonToMap(item));
                    } else if (item.isString()) {
                        list.add(item.asString());
                    } else if (item.isNumber()) {
                        list.add(item.asDouble());
                    } else if (item.isBoolean()) {
                        list.add(item.asBoolean());
                    }
                }
                map.put(key, list);
            } else if (entry.isString()) {
                map.put(key, entry.asString());
            } else if (entry.isNumber()) {
                map.put(key, entry.asDouble());
            } else if (entry.isBoolean()) {
                map.put(key, entry.asBoolean());
            } else if (entry.isNull()) {
                map.put(key, null);
            }
        }
        return map;
    }
}

