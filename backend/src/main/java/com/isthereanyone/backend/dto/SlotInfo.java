package com.isthereanyone.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SlotInfo {

    private Integer slotId;
    private boolean isEmpty;
    private LocalDateTime lastUpdated;

    private String currentMap;
    private String currentRoom;
    private Integer playerHp;
    private Integer maxHp;
    private Float playerStamina;
    private Float maxStamina;

    // Progress info
    private Integer currentLevel;
    private Integer deathCount;
    private Integer tasksCompleted;
    private Integer totalTasks;
    private Long playTime; // dalam milliseconds

    // Items info
    private Integer itemCount;
    private String currentlyHeldItem;

    public SlotInfo() {}

    public SlotInfo(Integer slotId, boolean isEmpty) {
        this.slotId = slotId;
        this.isEmpty = isEmpty;
    }

    @SuppressWarnings("unchecked")
    public static SlotInfo fromSaveData(Integer slotId, Map<String, Object> saveData, LocalDateTime lastUpdated) {
        SlotInfo info = new SlotInfo();
        info.setSlotId(slotId);
        info.setEmpty(false);
        info.setLastUpdated(lastUpdated);

        if (saveData != null) {
            // Direct fields (new structure)
            info.setCurrentMap(getStringValue(saveData, "currentMap"));
            info.setCurrentRoom(getStringValue(saveData, "currentRoom"));
            info.setPlayerHp(getIntValue(saveData, "playerHp"));
            info.setMaxHp(getIntValue(saveData, "maxHp"));
            info.setPlayerStamina(getFloatValue(saveData, "playerStamina"));
            info.setMaxStamina(getFloatValue(saveData, "maxStamina"));
            info.setCurrentLevel(getIntValue(saveData, "currentLevel"));
            info.setDeathCount(getIntValue(saveData, "deathCount"));
            info.setPlayTime(getLongValue(saveData, "playTime"));
            info.setCurrentlyHeldItem(getStringValue(saveData, "currentlyHeldItemId"));

            // Tasks completed count
            if (saveData.containsKey("completedTaskIds")) {
                List<?> completedTasks = (List<?>) saveData.get("completedTaskIds");
                info.setTasksCompleted(completedTasks != null ? completedTasks.size() : 0);
            }
            if (saveData.containsKey("totalTasksRequired")) {
                info.setTotalTasks(getIntValue(saveData, "totalTasksRequired"));
            }

            // Inventory count
            if (saveData.containsKey("inventoryItems")) {
                List<?> items = (List<?>) saveData.get("inventoryItems");
                info.setItemCount(items != null ? items.size() : 0);
            }

            // Legacy support: playerState structure
            if (saveData.containsKey("playerState")) {
                Map<String, Object> playerState = (Map<String, Object>) saveData.get("playerState");
                if (playerState != null) {
                    if (info.getCurrentMap() == null) {
                        info.setCurrentMap(getStringValue(playerState, "currentMap"));
                    }
                    if (info.getPlayerHp() == null) {
                        info.setPlayerHp(getIntValue(playerState, "hp"));
                    }
                }
            }

            // Legacy support: stats structure
            if (saveData.containsKey("stats")) {
                Map<String, Object> stats = (Map<String, Object>) saveData.get("stats");
                if (stats != null) {
                    if (info.getDeathCount() == null) {
                        info.setDeathCount(getIntValue(stats, "allTimeDeathCount"));
                    }
                    if (info.getTasksCompleted() == null) {
                        info.setTasksCompleted(getIntValue(stats, "allTimeCompletedTask"));
                    }
                }
            }
        }

        return info;
    }

    private static String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    private static Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Float getFloatValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).floatValue();
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static SlotInfo emptySlot(Integer slotId) {
        SlotInfo info = new SlotInfo();
        info.setSlotId(slotId);
        info.setEmpty(true);
        return info;
    }

    // ==================== GETTERS AND SETTERS ====================

    public Integer getSlotId() { return slotId; }
    public void setSlotId(Integer slotId) { this.slotId = slotId; }

    public boolean isEmpty() { return isEmpty; }
    public void setEmpty(boolean empty) { isEmpty = empty; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getCurrentMap() { return currentMap; }
    public void setCurrentMap(String currentMap) { this.currentMap = currentMap; }

    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }

    public Integer getPlayerHp() { return playerHp; }
    public void setPlayerHp(Integer playerHp) { this.playerHp = playerHp; }

    public Integer getMaxHp() { return maxHp; }
    public void setMaxHp(Integer maxHp) { this.maxHp = maxHp; }

    public Float getPlayerStamina() { return playerStamina; }
    public void setPlayerStamina(Float playerStamina) { this.playerStamina = playerStamina; }

    public Float getMaxStamina() { return maxStamina; }
    public void setMaxStamina(Float maxStamina) { this.maxStamina = maxStamina; }

    public Integer getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(Integer currentLevel) { this.currentLevel = currentLevel; }

    public Integer getDeathCount() { return deathCount; }
    public void setDeathCount(Integer deathCount) { this.deathCount = deathCount; }

    public Integer getTasksCompleted() { return tasksCompleted; }
    public void setTasksCompleted(Integer tasksCompleted) { this.tasksCompleted = tasksCompleted; }

    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }

    public Long getPlayTime() { return playTime; }
    public void setPlayTime(Long playTime) { this.playTime = playTime; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public String getCurrentlyHeldItem() { return currentlyHeldItem; }
    public void setCurrentlyHeldItem(String currentlyHeldItem) { this.currentlyHeldItem = currentlyHeldItem; }

    // Legacy getters for backward compatibility
    public Integer getAllTimeDeathCount() { return deathCount; }
    public void setAllTimeDeathCount(Integer count) { this.deathCount = count; }

    public Integer getAllTimeCompletedTask() { return tasksCompleted; }
    public void setAllTimeCompletedTask(Integer count) { this.tasksCompleted = count; }
}

