package com.isthereanyone.frontend.network.dto;

/**
 * DTO untuk informasi slot save game
 */
public class SlotInfo {
    private Integer slotId;
    private boolean isEmpty;
    private String lastUpdated;
    private String currentMap;
    private Integer allTimeDeathCount;
    private Integer allTimeCompletedTask;

    public SlotInfo() {}

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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
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

