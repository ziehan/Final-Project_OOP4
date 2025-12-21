package com.isthereanyone.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO untuk menampilkan info slot tanpa full save data
 * Berguna untuk list slots overview
 */
public class SlotInfo {

    private Integer slotId;
    private boolean isEmpty;
    private LocalDateTime lastUpdated;

    // Preview data dari saveData (untuk tampilan di menu)
    private String currentMap;
    private Integer allTimeDeathCount;
    private Integer allTimeCompletedTask;

    // Constructor
    public SlotInfo() {}

    public SlotInfo(Integer slotId, boolean isEmpty) {
        this.slotId = slotId;
        this.isEmpty = isEmpty;
    }

    /**
     * Factory method untuk membuat SlotInfo dari saveData
     */
    public static SlotInfo fromSaveData(Integer slotId, Map<String, Object> saveData, LocalDateTime lastUpdated) {
        SlotInfo info = new SlotInfo();
        info.setSlotId(slotId);
        info.setEmpty(false);
        info.setLastUpdated(lastUpdated);

        // Extract preview data
        if (saveData != null) {
            // Get currentMap from playerState
            if (saveData.containsKey("playerState")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> playerState = (Map<String, Object>) saveData.get("playerState");
                if (playerState != null && playerState.containsKey("currentMap")) {
                    info.setCurrentMap((String) playerState.get("currentMap"));
                }
            }

            // Get stats
            if (saveData.containsKey("stats")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> stats = (Map<String, Object>) saveData.get("stats");
                if (stats != null) {
                    if (stats.containsKey("allTimeDeathCount")) {
                        info.setAllTimeDeathCount(((Number) stats.get("allTimeDeathCount")).intValue());
                    }
                    if (stats.containsKey("allTimeCompletedTask")) {
                        info.setAllTimeCompletedTask(((Number) stats.get("allTimeCompletedTask")).intValue());
                    }
                }
            }
        }

        return info;
    }

    /**
     * Factory method untuk empty slot
     */
    public static SlotInfo emptySlot(Integer slotId) {
        SlotInfo info = new SlotInfo();
        info.setSlotId(slotId);
        info.setEmpty(true);
        return info;
    }

    // Getters and Setters
    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(String currentMap) {
        this.currentMap = currentMap;
    }

    public Integer getAllTimeDeathCount() {
        return allTimeDeathCount;
    }

    public void setAllTimeDeathCount(Integer allTimeDeathCount) {
        this.allTimeDeathCount = allTimeDeathCount;
    }

    public Integer getAllTimeCompletedTask() {
        return allTimeCompletedTask;
    }

    public void setAllTimeCompletedTask(Integer allTimeCompletedTask) {
        this.allTimeCompletedTask = allTimeCompletedTask;
    }
}

