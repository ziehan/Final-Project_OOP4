package com.isthereanyone.frontend.managers;

import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.network.NetworkCallback;
import com.isthereanyone.frontend.network.dto.ApiResponse;
import com.isthereanyone.frontend.network.dto.GameSaveResponse;
import com.isthereanyone.frontend.network.dto.SlotInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStateManager {
    private static GameStateManager instance;

    private int allTimeDeathCount = 0;
    private int allTimeCompletedTask = 0;
    private int currentSessionDeathCount = 0;
    private int currentSessionCompletedTask = 0;

    private String currentMap = "Tilemap.tmx";
    private float playerX = 400f;
    private float playerY = 400f;
    private int playerHealth = 2;

    private GameStateManager() {}

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public void onPlayerDeath() {
        allTimeDeathCount++;
        currentSessionDeathCount++;
        System.out.println("[GameStateManager] Player died. Total deaths: " + allTimeDeathCount);
    }

    public void onTaskCompleted() {
        allTimeCompletedTask++;
        currentSessionCompletedTask++;
        System.out.println("[GameStateManager] Task completed. Total tasks: " + allTimeCompletedTask);
    }

    public void resetSessionStats() {
        currentSessionDeathCount = 0;
        currentSessionCompletedTask = 0;
    }

    public void updateFromPlayer(Player player) {
        if (player != null) {
            this.playerX = player.position.x;
            this.playerY = player.position.y;
            this.playerHealth = player.health;
        }
    }

    public Map<String, Object> buildSaveData() {
        Map<String, Object> saveData = new HashMap<>();

        Map<String, Object> playerState = new HashMap<>();
        playerState.put("x", playerX);
        playerState.put("y", playerY);
        playerState.put("health", playerHealth);
        playerState.put("currentMap", currentMap);
        saveData.put("playerState", playerState);

        Map<String, Object> stats = new HashMap<>();
        stats.put("allTimeDeathCount", allTimeDeathCount);
        stats.put("allTimeCompletedTask", allTimeCompletedTask);
        stats.put("sessionDeathCount", currentSessionDeathCount);
        stats.put("sessionCompletedTask", currentSessionCompletedTask);
        saveData.put("stats", stats);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("saveVersion", 1);
        metadata.put("timestamp", System.currentTimeMillis());
        saveData.put("metadata", metadata);

        return saveData;
    }

    @SuppressWarnings("unchecked")
    public void loadFromSaveData(Map<String, Object> saveData) {
        if (saveData == null) return;

        if (saveData.containsKey("playerState")) {
            Map<String, Object> playerState = (Map<String, Object>) saveData.get("playerState");
            if (playerState != null) {
                this.playerX = getFloatValue(playerState.get("x"), 400f);
                this.playerY = getFloatValue(playerState.get("y"), 400f);
                this.playerHealth = getIntValue(playerState.get("health"), 2);
                this.currentMap = (String) playerState.getOrDefault("currentMap", "Tilemap.tmx");
            }
        }

        if (saveData.containsKey("stats")) {
            Map<String, Object> stats = (Map<String, Object>) saveData.get("stats");
            if (stats != null) {
                this.allTimeDeathCount = getIntValue(stats.get("allTimeDeathCount"), 0);
                this.allTimeCompletedTask = getIntValue(stats.get("allTimeCompletedTask"), 0);
            }
        }

        System.out.println("[GameStateManager] Loaded save data - Deaths: " + allTimeDeathCount +
            ", Tasks: " + allTimeCompletedTask);
    }

    public void saveToSlot(int slotId, NetworkCallback<Boolean> callback) {
        NetworkManager.getInstance().saveGame(slotId, buildSaveData(),
            new NetworkCallback<ApiResponse<GameSaveResponse>>() {
                @Override
                public void onSuccess(ApiResponse<GameSaveResponse> result) {
                    if (result.isSuccess()) {
                        System.out.println("[GameStateManager] Game saved to slot " + slotId);
                        callback.onSuccess(true);
                    } else {
                        callback.onFailure(result.getMessage());
                    }
                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure(error);
                }
            });
    }

    public void loadFromSlot(int slotId, NetworkCallback<Boolean> callback) {
        NetworkManager.getInstance().loadGame(slotId,
            new NetworkCallback<ApiResponse<GameSaveResponse>>() {
                @Override
                public void onSuccess(ApiResponse<GameSaveResponse> result) {
                    if (result.isSuccess() && result.getData() != null) {
                        loadFromSaveData(result.getData().getSaveData());
                        System.out.println("[GameStateManager] Game loaded from slot " + slotId);
                        callback.onSuccess(true);
                    } else {
                        callback.onFailure(result.getMessage());
                    }
                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure(error);
                }
            });
    }

    public void getSlotInfos(NetworkCallback<List<SlotInfo>> callback) {
        NetworkManager.getInstance().getAllSlots(
            new NetworkCallback<ApiResponse<List<SlotInfo>>>() {
                @Override
                public void onSuccess(ApiResponse<List<SlotInfo>> result) {
                    if (result.isSuccess() && result.getData() != null) {
                        callback.onSuccess(result.getData());
                    } else {
                        callback.onFailure(result.getMessage());
                    }
                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure(error);
                }
            });
    }

    public void deleteSlot(int slotId, NetworkCallback<Boolean> callback) {
        NetworkManager.getInstance().deleteSlot(slotId,
            new NetworkCallback<ApiResponse<Void>>() {
                @Override
                public void onSuccess(ApiResponse<Void> result) {
                    if (result.isSuccess()) {
                        System.out.println("[GameStateManager] Slot " + slotId + " deleted");
                        callback.onSuccess(true);
                    } else {
                        callback.onFailure(result.getMessage());
                    }
                }

                @Override
                public void onFailure(String error) {
                    callback.onFailure(error);
                }
            });
    }

    public int getAllTimeDeathCount() {
        return allTimeDeathCount;
    }

    public int getAllTimeCompletedTask() {
        return allTimeCompletedTask;
    }

    public int getCurrentSessionDeathCount() {
        return currentSessionDeathCount;
    }

    public int getCurrentSessionCompletedTask() {
        return currentSessionCompletedTask;
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public float getPlayerX() {
        return playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public int getPlayerHealth() {
        return playerHealth;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public void setPlayerPosition(float x, float y) {
        this.playerX = x;
        this.playerY = y;
    }

    public void setPlayerHealth(int health) {
        this.playerHealth = health;
    }

    private float getFloatValue(Object value, float defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }

    private int getIntValue(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    public void resetToDefault() {
        this.currentMap = "Tilemap.tmx";
        this.playerX = 400f;
        this.playerY = 400f;
        this.playerHealth = 2;
        resetSessionStats();
        System.out.println("[GameStateManager] State reset to default");
    }
}

