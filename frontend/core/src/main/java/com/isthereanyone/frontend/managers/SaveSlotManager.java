package com.isthereanyone.frontend.managers;

import com.isthereanyone.frontend.network.NetworkCallback;
import com.isthereanyone.frontend.network.dto.ApiResponse;
import com.isthereanyone.frontend.network.dto.GameSaveResponse;
import com.isthereanyone.frontend.network.dto.SlotInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SaveSlotManager - Manages game save slots
 * Now connected to backend via NetworkManager
 */
public class SaveSlotManager {
    private static SaveSlotManager instance;
    private static final int TOTAL_SLOTS = 3;
    private int currentSlot = -1;
    private Map<Integer, SaveSlotData> slots = new HashMap<>();
    private boolean isLoading = false;

    private SaveSlotManager() {
        initializeSlots();
    }

    public static SaveSlotManager getInstance() {
        if (instance == null) {
            instance = new SaveSlotManager();
        }
        return instance;
    }

    private void initializeSlots() {
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            slots.put(i, new SaveSlotData(i));
        }
    }

    // ==================== ASYNC METHODS (Connected to Backend) ====================

    public void loadSlotsFromBackend(NetworkCallback<Boolean> callback) {
        if (!NetworkManager.getInstance().isLoggedIn()) {
            callback.onFailure("User not logged in");
            return;
        }

        isLoading = true;
        NetworkManager.getInstance().getAllSlots(new NetworkCallback<ApiResponse<List<SlotInfo>>>() {
            @Override
            public void onSuccess(ApiResponse<List<SlotInfo>> result) {
                isLoading = false;
                if (result.isSuccess() && result.getData() != null) {
                    // Update local slots with backend data
                    for (SlotInfo info : result.getData()) {
                        SaveSlotData localSlot = slots.get(info.getSlotId());
                        if (localSlot != null) {
                            localSlot.setHasData(!info.isEmpty());
                            if (info.getLastUpdated() != null) {
                                localSlot.setLastUpdatedStr(info.getLastUpdated());
                            }
                        }
                    }
                    callback.onSuccess(true);
                } else {
                    callback.onFailure(result.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                isLoading = false;
                callback.onFailure(error);
            }
        });
    }

    public void saveGameToBackend(int playerLevel, int playerHP, float playerX, float playerY,
                                   NetworkCallback<Boolean> callback) {
        if (currentSlot == -1) {
            callback.onFailure("No slot selected");
            return;
        }

        Map<String, Object> saveData = new HashMap<>();

        Map<String, Object> playerState = new HashMap<>();
        playerState.put("level", playerLevel);
        playerState.put("hp", playerHP);
        playerState.put("posX", playerX);
        playerState.put("posY", playerY);
        saveData.put("playerState", playerState);

        Map<String, Object> stats = new HashMap<>();
        stats.put("lastSavedTime", System.currentTimeMillis());
        saveData.put("stats", stats);

        NetworkManager.getInstance().saveGame(currentSlot, saveData,
            new NetworkCallback<ApiResponse<GameSaveResponse>>() {
                @Override
                public void onSuccess(ApiResponse<GameSaveResponse> result) {
                    if (result.isSuccess()) {
                        // Update local slot
                        SaveSlotData slot = slots.get(currentSlot);
                        slot.setPlayerLevel(playerLevel);
                        slot.setPlayerHP(playerHP);
                        slot.setPlayerX(playerX);
                        slot.setPlayerY(playerY);
                        slot.setLastSavedTime(System.currentTimeMillis());
                        slot.setHasData(true);

                        System.out.println("[SAVE] Game saved to backend slot " + currentSlot);
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

    public void loadGameFromBackend(NetworkCallback<SaveSlotData> callback) {
        if (currentSlot == -1) {
            callback.onFailure("No slot selected");
            return;
        }

        NetworkManager.getInstance().loadGame(currentSlot,
            new NetworkCallback<ApiResponse<GameSaveResponse>>() {
                @Override
                public void onSuccess(ApiResponse<GameSaveResponse> result) {
                    if (result.isSuccess() && result.getData() != null) {
                        GameSaveResponse data = result.getData();
                        SaveSlotData slot = slots.get(currentSlot);

                        // Parse save data
                        Map<String, Object> saveData = data.getSaveData();
                        if (saveData != null && saveData.containsKey("playerState")) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> playerState = (Map<String, Object>) saveData.get("playerState");
                            if (playerState != null) {
                                slot.setPlayerLevel(getIntValue(playerState, "level", 1));
                                slot.setPlayerHP(getIntValue(playerState, "hp", 100));
                                slot.setPlayerX(getFloatValue(playerState, "posX", 0f));
                                slot.setPlayerY(getFloatValue(playerState, "posY", 0f));
                            }
                        }
                        slot.setHasData(true);

                        System.out.println("[SAVE] Game loaded from backend slot " + currentSlot);
                        callback.onSuccess(slot);
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

    public void deleteSlotFromBackend(int slotNumber, NetworkCallback<Boolean> callback) {
        if (slotNumber < 1 || slotNumber > TOTAL_SLOTS) {
            callback.onFailure("Invalid slot number");
            return;
        }

        NetworkManager.getInstance().deleteSlot(slotNumber,
            new NetworkCallback<ApiResponse<Void>>() {
                @Override
                public void onSuccess(ApiResponse<Void> result) {
                    if (result.isSuccess()) {
                        slots.get(slotNumber).resetData();
                        if (currentSlot == slotNumber) {
                            currentSlot = -1;
                        }
                        System.out.println("[SAVE] Slot " + slotNumber + " deleted from backend");
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

    // ==================== SYNC METHODS (Local only, backward compatibility) ====================

    public boolean loadSavesForUser(String username) {
        System.out.println("[SAVE] Loading save slots for user: " + username);
        return true;
    }

    public SaveSlotData getSlot(int slotNumber) {
        if (slotNumber < 1 || slotNumber > TOTAL_SLOTS) {
            System.out.println("[SAVE] Invalid slot number: " + slotNumber);
            return null;
        }
        return slots.get(slotNumber);
    }

    public boolean selectSlot(int slotNumber) {
        if (slotNumber < 1 || slotNumber > TOTAL_SLOTS) {
            System.out.println("[SAVE] Invalid slot number: " + slotNumber);
            return false;
        }
        currentSlot = slotNumber;
        System.out.println("[SAVE] Slot " + slotNumber + " selected");
        return true;
    }

    public boolean saveGame(int playerLevel, int playerHP, float playerX, float playerY) {
        if (currentSlot == -1) {
            System.out.println("[SAVE] No slot selected");
            return false;
        }

        SaveSlotData slot = slots.get(currentSlot);
        slot.setPlayerLevel(playerLevel);
        slot.setPlayerHP(playerHP);
        slot.setPlayerX(playerX);
        slot.setPlayerY(playerY);
        slot.setLastSavedTime(System.currentTimeMillis());
        slot.setHasData(true);

        if (NetworkManager.getInstance().isLoggedIn()) {
            Map<String, Object> saveData = new HashMap<>();
            Map<String, Object> playerState = new HashMap<>();
            playerState.put("level", playerLevel);
            playerState.put("hp", playerHP);
            playerState.put("posX", playerX);
            playerState.put("posY", playerY);
            saveData.put("playerState", playerState);

            NetworkManager.getInstance().saveGame(currentSlot, saveData,
                new NetworkCallback<ApiResponse<GameSaveResponse>>() {
                    @Override
                    public void onSuccess(ApiResponse<GameSaveResponse> result) {
                        System.out.println("[SAVE] Game synced to backend");
                    }

                    @Override
                    public void onFailure(String error) {
                        System.out.println("[SAVE] Failed to sync to backend: " + error);
                    }
                });
        }

        System.out.println("[SAVE] Game saved to slot " + currentSlot);
        return true;
    }

    public SaveSlotData loadGame() {
        if (currentSlot == -1) {
            System.out.println("[SAVE] No slot selected");
            return null;
        }

        SaveSlotData slot = slots.get(currentSlot);
        if (!slot.hasData()) {
            System.out.println("[SAVE] Slot " + currentSlot + " is empty");
            return null;
        }

        System.out.println("[SAVE] Game loaded from slot " + currentSlot);
        return slot;
    }

    public boolean newGame() {
        if (currentSlot == -1) {
            System.out.println("[SAVE] No slot selected");
            return false;
        }

        SaveSlotData slot = slots.get(currentSlot);
        slot.resetData();
        slot.setHasData(true);
        System.out.println("[SAVE] New game started in slot " + currentSlot);
        return true;
    }

    public boolean deleteSlot(int slotNumber) {
        if (slotNumber < 1 || slotNumber > TOTAL_SLOTS) {
            System.out.println("[SAVE] Invalid slot number: " + slotNumber);
            return false;
        }

        slots.get(slotNumber).resetData();
        if (currentSlot == slotNumber) {
            currentSlot = -1;
        }

        System.out.println("[SAVE] Slot " + slotNumber + " deleted");
        return true;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public int getTotalSlots() {
        return TOTAL_SLOTS;
    }

    public boolean isLoading() {
        return isLoading;
    }

    // ==================== HELPER METHODS ====================

    private int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        return defaultValue;
    }

    private float getFloatValue(Map<String, Object> map, String key, float defaultValue) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
        }
        return defaultValue;
    }

    // ==================== INNER CLASS ====================

    public static class SaveSlotData {
        private int slotNumber;
        private boolean hasData;
        private int playerLevel;
        private int playerHP;
        private float playerX;
        private float playerY;
        private long lastSavedTime;
        private String lastUpdatedStr;
        private String characterName;

        public SaveSlotData(int slotNumber) {
            this.slotNumber = slotNumber;
            this.hasData = false;
            this.playerLevel = 1;
            this.playerHP = 100;
            this.playerX = 0f;
            this.playerY = 0f;
            this.lastSavedTime = 0;
            this.characterName = "New Game";
        }

        public void resetData() {
            this.hasData = false;
            this.playerLevel = 1;
            this.playerHP = 100;
            this.playerX = 0f;
            this.playerY = 0f;
            this.lastSavedTime = 0;
            this.characterName = "New Game";
        }

        public int getSlotNumber() { return slotNumber; }
        public boolean hasData() { return hasData; }
        public void setHasData(boolean hasData) { this.hasData = hasData; }

        public int getPlayerLevel() { return playerLevel; }
        public void setPlayerLevel(int playerLevel) { this.playerLevel = playerLevel; }

        public int getPlayerHP() { return playerHP; }
        public void setPlayerHP(int playerHP) { this.playerHP = playerHP; }

        public float getPlayerX() { return playerX; }
        public void setPlayerX(float playerX) { this.playerX = playerX; }

        public float getPlayerY() { return playerY; }
        public void setPlayerY(float playerY) { this.playerY = playerY; }

        public long getLastSavedTime() { return lastSavedTime; }
        public void setLastSavedTime(long lastSavedTime) { this.lastSavedTime = lastSavedTime; }

        public String getLastUpdatedStr() { return lastUpdatedStr; }
        public void setLastUpdatedStr(String lastUpdatedStr) { this.lastUpdatedStr = lastUpdatedStr; }

        public String getCharacterName() { return characterName; }
        public void setCharacterName(String characterName) { this.characterName = characterName; }

        @Override
        public String toString() {
            if (!hasData) {
                return "Slot " + slotNumber + " - Empty";
            }
            return "Slot " + slotNumber + " - Level: " + playerLevel + ", HP: " + playerHP;
        }
    }
}
