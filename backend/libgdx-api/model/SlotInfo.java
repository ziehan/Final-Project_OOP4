package com.isthereanyone.api.model;

/**
 * Model class untuk info Slot
 * Digunakan untuk menampilkan daftar save slots di menu
 */
public class SlotInfo {
    private int slotId;
    private boolean isEmpty;
    private String lastUpdated;
    private String currentMap;
    private Integer allTimeDeathCount;
    private Integer allTimeCompletedTask;

    public SlotInfo() {}

    public SlotInfo(int slotId, boolean isEmpty) {
        this.slotId = slotId;
        this.isEmpty = isEmpty;
    }

    // Getters and Setters
    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public boolean isEmpty() { return isEmpty; }
    public void setEmpty(boolean empty) { isEmpty = empty; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getCurrentMap() { return currentMap; }
    public void setCurrentMap(String currentMap) { this.currentMap = currentMap; }

    public Integer getAllTimeDeathCount() { return allTimeDeathCount; }
    public void setAllTimeDeathCount(Integer allTimeDeathCount) { this.allTimeDeathCount = allTimeDeathCount; }

    public Integer getAllTimeCompletedTask() { return allTimeCompletedTask; }
    public void setAllTimeCompletedTask(Integer allTimeCompletedTask) { this.allTimeCompletedTask = allTimeCompletedTask; }

    @Override
    public String toString() {
        if (isEmpty) {
            return "Slot " + slotId + ": Empty";
        }
        return "Slot " + slotId + ": " + currentMap + " - Deaths: " + allTimeDeathCount;
    }
}

