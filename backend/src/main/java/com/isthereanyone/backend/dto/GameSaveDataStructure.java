package com.isthereanyone.backend.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * DTO lengkap untuk struktur Save Data
 * Ini adalah dokumentasi struktur yang diharapkan dari frontend
 *
 * Backend menggunakan Map<String, Object> untuk flexibility,
 * tapi frontend HARUS mengirim data dengan struktur ini
 */
public class GameSaveDataStructure {

    // ==================== PLAYER DATA ====================
    private Integer playerHp;
    private Integer maxHp;
    private Float playerX;
    private Float playerY;
    private String playerDirection; // "up", "down", "left", "right"
    private Boolean isHiding;

    // ==================== LEVEL/ROOM DATA ====================
    private Integer currentLevel;
    private String currentRoom;
    private String currentMap; // nama file .tmx
    private List<Integer> completedLevels;
    private List<String> unlockedRooms;

    // ==================== GHOST DATA ====================
    private List<GhostData> ghosts; // support multiple ghosts

    // ==================== TASK DATA ====================
    private List<TaskData> tasks;
    private List<String> completedTaskIds;
    private String currentActiveTaskId;
    private Integer totalTasksCompleted;
    private Integer totalTasksRequired;

    // ==================== INVENTORY DATA ====================
    private List<ItemData> inventoryItems;
    private String currentlyHeldItemId; // item yang sedang dipegang
    private Integer inventoryCapacity;

    // ==================== GAME PROGRESS ====================
    private Integer coins;
    private Long playTime; // dalam milliseconds
    private Integer deathCount;
    private Integer saveCount;

    // ==================== SPAWN SETTINGS ====================
    private Boolean useCustomSpawn; // true = pakai posisi dari save
    private Float customSpawnX;
    private Float customSpawnY;
    private String spawnRoom;

    // ==================== TIMESTAMPS ====================
    private Long saveTimestamp;
    private Long lastPlayedTimestamp;

    // Default constructor
    public GameSaveDataStructure() {
        this.ghosts = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.completedTaskIds = new ArrayList<>();
        this.completedLevels = new ArrayList<>();
        this.unlockedRooms = new ArrayList<>();
        this.inventoryItems = new ArrayList<>();
        this.useCustomSpawn = false;
    }

    // ==================== GETTERS AND SETTERS ====================

    // Player
    public Integer getPlayerHp() { return playerHp; }
    public void setPlayerHp(Integer playerHp) { this.playerHp = playerHp; }

    public Integer getMaxHp() { return maxHp; }
    public void setMaxHp(Integer maxHp) { this.maxHp = maxHp; }

    public Float getPlayerX() { return playerX; }
    public void setPlayerX(Float playerX) { this.playerX = playerX; }

    public Float getPlayerY() { return playerY; }
    public void setPlayerY(Float playerY) { this.playerY = playerY; }

    public String getPlayerDirection() { return playerDirection; }
    public void setPlayerDirection(String playerDirection) { this.playerDirection = playerDirection; }

    public Boolean getIsHiding() { return isHiding; }
    public void setIsHiding(Boolean isHiding) { this.isHiding = isHiding; }

    // Level/Room
    public Integer getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(Integer currentLevel) { this.currentLevel = currentLevel; }

    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }

    public String getCurrentMap() { return currentMap; }
    public void setCurrentMap(String currentMap) { this.currentMap = currentMap; }

    public List<Integer> getCompletedLevels() { return completedLevels; }
    public void setCompletedLevels(List<Integer> completedLevels) { this.completedLevels = completedLevels; }

    public List<String> getUnlockedRooms() { return unlockedRooms; }
    public void setUnlockedRooms(List<String> unlockedRooms) { this.unlockedRooms = unlockedRooms; }

    // Ghost
    public List<GhostData> getGhosts() { return ghosts; }
    public void setGhosts(List<GhostData> ghosts) { this.ghosts = ghosts; }

    // Tasks
    public List<TaskData> getTasks() { return tasks; }
    public void setTasks(List<TaskData> tasks) { this.tasks = tasks; }

    public List<String> getCompletedTaskIds() { return completedTaskIds; }
    public void setCompletedTaskIds(List<String> completedTaskIds) { this.completedTaskIds = completedTaskIds; }

    public String getCurrentActiveTaskId() { return currentActiveTaskId; }
    public void setCurrentActiveTaskId(String currentActiveTaskId) { this.currentActiveTaskId = currentActiveTaskId; }

    public Integer getTotalTasksCompleted() { return totalTasksCompleted; }
    public void setTotalTasksCompleted(Integer totalTasksCompleted) { this.totalTasksCompleted = totalTasksCompleted; }

    public Integer getTotalTasksRequired() { return totalTasksRequired; }
    public void setTotalTasksRequired(Integer totalTasksRequired) { this.totalTasksRequired = totalTasksRequired; }

    // Inventory
    public List<ItemData> getInventoryItems() { return inventoryItems; }
    public void setInventoryItems(List<ItemData> inventoryItems) { this.inventoryItems = inventoryItems; }

    public String getCurrentlyHeldItemId() { return currentlyHeldItemId; }
    public void setCurrentlyHeldItemId(String currentlyHeldItemId) { this.currentlyHeldItemId = currentlyHeldItemId; }

    public Integer getInventoryCapacity() { return inventoryCapacity; }
    public void setInventoryCapacity(Integer inventoryCapacity) { this.inventoryCapacity = inventoryCapacity; }

    // Progress
    public Integer getCoins() { return coins; }
    public void setCoins(Integer coins) { this.coins = coins; }

    public Long getPlayTime() { return playTime; }
    public void setPlayTime(Long playTime) { this.playTime = playTime; }

    public Integer getDeathCount() { return deathCount; }
    public void setDeathCount(Integer deathCount) { this.deathCount = deathCount; }

    public Integer getSaveCount() { return saveCount; }
    public void setSaveCount(Integer saveCount) { this.saveCount = saveCount; }

    // Spawn
    public Boolean getUseCustomSpawn() { return useCustomSpawn; }
    public void setUseCustomSpawn(Boolean useCustomSpawn) { this.useCustomSpawn = useCustomSpawn; }

    public Float getCustomSpawnX() { return customSpawnX; }
    public void setCustomSpawnX(Float customSpawnX) { this.customSpawnX = customSpawnX; }

    public Float getCustomSpawnY() { return customSpawnY; }
    public void setCustomSpawnY(Float customSpawnY) { this.customSpawnY = customSpawnY; }

    public String getSpawnRoom() { return spawnRoom; }
    public void setSpawnRoom(String spawnRoom) { this.spawnRoom = spawnRoom; }

    // Timestamps
    public Long getSaveTimestamp() { return saveTimestamp; }
    public void setSaveTimestamp(Long saveTimestamp) { this.saveTimestamp = saveTimestamp; }

    public Long getLastPlayedTimestamp() { return lastPlayedTimestamp; }
    public void setLastPlayedTimestamp(Long lastPlayedTimestamp) { this.lastPlayedTimestamp = lastPlayedTimestamp; }
}

