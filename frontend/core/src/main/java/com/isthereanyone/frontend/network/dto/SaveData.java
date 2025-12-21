package com.isthereanyone.frontend.network.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class lengkap untuk Save Data
 * Digunakan untuk save/load game state ke backend
 */
public class SaveData {

    // ==================== PLAYER DATA ====================
    private int playerHp;
    private int maxHp;
    private Float playerStamina;
    private Float maxStamina;
    private float playerX;
    private float playerY;
    private String playerDirection; // "up", "down", "left", "right"
    private boolean isHiding;

    // ==================== LEVEL/ROOM DATA ====================
    private int currentLevel;
    private String currentRoom;
    private String currentMap; // nama file .tmx
    private List<Integer> completedLevels;
    private List<String> unlockedRooms;

    // ==================== GHOST DATA ====================
    private List<GhostData> ghosts;

    // ==================== TASK DATA ====================
    private List<TaskData> tasks;
    private List<String> completedTaskIds;
    private String currentActiveTaskId;
    private int totalTasksCompleted;
    private int totalTasksRequired;

    // ==================== INVENTORY DATA ====================
    private List<ItemData> inventoryItems;
    private String currentlyHeldItemId;
    private int inventoryCapacity;

    // ==================== GAME PROGRESS ====================
    private int coins;
    private long playTime; // dalam milliseconds
    private int deathCount;
    private int saveCount;

    // ==================== SPAWN SETTINGS ====================
    private boolean useCustomSpawn; // true = pakai posisi dari save
    private float customSpawnX;
    private float customSpawnY;
    private String spawnRoom;

    // ==================== TIMESTAMPS ====================
    private long saveTimestamp;
    private long lastPlayedTimestamp;

    public SaveData() {
        // Initialize defaults
        this.playerHp = 3;
        this.maxHp = 3;
        this.currentLevel = 1;
        this.ghosts = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.completedTaskIds = new ArrayList<>();
        this.completedLevels = new ArrayList<>();
        this.unlockedRooms = new ArrayList<>();
        this.inventoryItems = new ArrayList<>();
        this.useCustomSpawn = false;
        this.inventoryCapacity = 5;
    }

    // ==================== PLAYER GETTERS/SETTERS ====================

    public int getPlayerHp() { return playerHp; }
    public void setPlayerHp(int playerHp) { this.playerHp = playerHp; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public Float getPlayerStamina() { return playerStamina; }
    public void setPlayerStamina(Float playerStamina) { this.playerStamina = playerStamina; }

    public Float getMaxStamina() { return maxStamina; }
    public void setMaxStamina(Float maxStamina) { this.maxStamina = maxStamina; }

    public float getPlayerX() { return playerX; }
    public void setPlayerX(float playerX) { this.playerX = playerX; }

    public float getPlayerY() { return playerY; }
    public void setPlayerY(float playerY) { this.playerY = playerY; }

    public String getPlayerDirection() { return playerDirection; }
    public void setPlayerDirection(String playerDirection) { this.playerDirection = playerDirection; }

    public boolean isHiding() { return isHiding; }
    public void setHiding(boolean hiding) { isHiding = hiding; }

    // ==================== LEVEL GETTERS/SETTERS ====================

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }

    public String getCurrentMap() { return currentMap; }
    public void setCurrentMap(String currentMap) { this.currentMap = currentMap; }

    public List<Integer> getCompletedLevels() { return completedLevels; }
    public void setCompletedLevels(List<Integer> completedLevels) { this.completedLevels = completedLevels; }

    public List<String> getUnlockedRooms() { return unlockedRooms; }
    public void setUnlockedRooms(List<String> unlockedRooms) { this.unlockedRooms = unlockedRooms; }

    // ==================== GHOST GETTERS/SETTERS ====================

    public List<GhostData> getGhosts() { return ghosts; }
    public void setGhosts(List<GhostData> ghosts) { this.ghosts = ghosts; }

    public void addGhost(GhostData ghost) {
        if (this.ghosts == null) this.ghosts = new ArrayList<>();
        this.ghosts.add(ghost);
    }

    // ==================== TASK GETTERS/SETTERS ====================

    public List<TaskData> getTasks() { return tasks; }
    public void setTasks(List<TaskData> tasks) { this.tasks = tasks; }

    public List<String> getCompletedTaskIds() { return completedTaskIds; }
    public void setCompletedTaskIds(List<String> completedTaskIds) { this.completedTaskIds = completedTaskIds; }

    public void addCompletedTask(String taskId) {
        if (this.completedTaskIds == null) this.completedTaskIds = new ArrayList<>();
        if (!this.completedTaskIds.contains(taskId)) {
            this.completedTaskIds.add(taskId);
        }
    }

    public String getCurrentActiveTaskId() { return currentActiveTaskId; }
    public void setCurrentActiveTaskId(String currentActiveTaskId) { this.currentActiveTaskId = currentActiveTaskId; }

    public int getTotalTasksCompleted() { return totalTasksCompleted; }
    public void setTotalTasksCompleted(int totalTasksCompleted) { this.totalTasksCompleted = totalTasksCompleted; }

    public int getTotalTasksRequired() { return totalTasksRequired; }
    public void setTotalTasksRequired(int totalTasksRequired) { this.totalTasksRequired = totalTasksRequired; }

    // ==================== INVENTORY GETTERS/SETTERS ====================

    public List<ItemData> getInventoryItems() { return inventoryItems; }
    public void setInventoryItems(List<ItemData> inventoryItems) { this.inventoryItems = inventoryItems; }

    public void addItem(ItemData item) {
        if (this.inventoryItems == null) this.inventoryItems = new ArrayList<>();
        this.inventoryItems.add(item);
    }

    public String getCurrentlyHeldItemId() { return currentlyHeldItemId; }
    public void setCurrentlyHeldItemId(String currentlyHeldItemId) { this.currentlyHeldItemId = currentlyHeldItemId; }

    public int getInventoryCapacity() { return inventoryCapacity; }
    public void setInventoryCapacity(int inventoryCapacity) { this.inventoryCapacity = inventoryCapacity; }

    // ==================== PROGRESS GETTERS/SETTERS ====================

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }

    public long getPlayTime() { return playTime; }
    public void setPlayTime(long playTime) { this.playTime = playTime; }

    public int getDeathCount() { return deathCount; }
    public void setDeathCount(int deathCount) { this.deathCount = deathCount; }

    public int getSaveCount() { return saveCount; }
    public void setSaveCount(int saveCount) { this.saveCount = saveCount; }

    // ==================== SPAWN GETTERS/SETTERS ====================

    public boolean isUseCustomSpawn() { return useCustomSpawn; }
    public void setUseCustomSpawn(boolean useCustomSpawn) { this.useCustomSpawn = useCustomSpawn; }

    public float getCustomSpawnX() { return customSpawnX; }
    public void setCustomSpawnX(float customSpawnX) { this.customSpawnX = customSpawnX; }

    public float getCustomSpawnY() { return customSpawnY; }
    public void setCustomSpawnY(float customSpawnY) { this.customSpawnY = customSpawnY; }

    public String getSpawnRoom() { return spawnRoom; }
    public void setSpawnRoom(String spawnRoom) { this.spawnRoom = spawnRoom; }

    // ==================== TIMESTAMP GETTERS/SETTERS ====================

    public long getSaveTimestamp() { return saveTimestamp; }
    public void setSaveTimestamp(long saveTimestamp) { this.saveTimestamp = saveTimestamp; }

    public long getLastPlayedTimestamp() { return lastPlayedTimestamp; }
    public void setLastPlayedTimestamp(long lastPlayedTimestamp) { this.lastPlayedTimestamp = lastPlayedTimestamp; }

    // ==================== HELPER METHODS ====================

    /**
     * Cek apakah task sudah selesai
     */
    public boolean isTaskCompleted(String taskId) {
        return completedTaskIds != null && completedTaskIds.contains(taskId);
    }

    /**
     * Cek apakah punya item tertentu
     */
    public boolean hasItem(String itemId) {
        if (inventoryItems == null) return false;
        for (ItemData item : inventoryItems) {
            if (item.getItemId().equals(itemId)) return true;
        }
        return false;
    }

    /**
     * Get item by ID
     */
    public ItemData getItem(String itemId) {
        if (inventoryItems == null) return null;
        for (ItemData item : inventoryItems) {
            if (item.getItemId().equals(itemId)) return item;
        }
        return null;
    }

    /**
     * Format play time as string (e.g., "1h 30m")
     */
    public String getFormattedPlayTime() {
        long seconds = playTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
}

