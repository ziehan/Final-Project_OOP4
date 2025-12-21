package com.isthereanyone.api.model;

/**
 * Model class untuk info Slot
 * Digunakan untuk menampilkan daftar save slots di menu
 * Copy ke project frontend LibGDX
 */
public class SlotInfo {
    private int slotId;
    private boolean isEmpty;
    private String lastUpdated;

    // Player info
    private String currentMap;
    private String currentRoom;
    private int playerHp;
    private int maxHp;
    private float playerStamina;
    private float maxStamina;

    // Progress info
    private int currentLevel;
    private int deathCount;
    private int tasksCompleted;
    private int totalTasks;
    private long playTime;

    // Items info
    private int itemCount;
    private String currentlyHeldItem;

    public SlotInfo() {}

    public SlotInfo(int slotId, boolean isEmpty) {
        this.slotId = slotId;
        this.isEmpty = isEmpty;
    }

    // ==================== GETTERS AND SETTERS ====================

    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public boolean isEmpty() { return isEmpty; }
    public void setEmpty(boolean empty) { isEmpty = empty; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getCurrentMap() { return currentMap; }
    public void setCurrentMap(String currentMap) { this.currentMap = currentMap; }

    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }

    public int getPlayerHp() { return playerHp; }
    public void setPlayerHp(int playerHp) { this.playerHp = playerHp; }

    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }

    public float getPlayerStamina() { return playerStamina; }
    public void setPlayerStamina(float playerStamina) { this.playerStamina = playerStamina; }

    public float getMaxStamina() { return maxStamina; }
    public void setMaxStamina(float maxStamina) { this.maxStamina = maxStamina; }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

    public int getDeathCount() { return deathCount; }
    public void setDeathCount(int deathCount) { this.deathCount = deathCount; }

    public int getTasksCompleted() { return tasksCompleted; }
    public void setTasksCompleted(int tasksCompleted) { this.tasksCompleted = tasksCompleted; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public long getPlayTime() { return playTime; }
    public void setPlayTime(long playTime) { this.playTime = playTime; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getCurrentlyHeldItem() { return currentlyHeldItem; }
    public void setCurrentlyHeldItem(String currentlyHeldItem) { this.currentlyHeldItem = currentlyHeldItem; }

    // Legacy getters for backward compatibility
    public int getAllTimeDeathCount() { return deathCount; }
    public void setAllTimeDeathCount(int count) { this.deathCount = count; }

    public int getAllTimeCompletedTask() { return tasksCompleted; }
    public void setAllTimeCompletedTask(int count) { this.tasksCompleted = count; }

    // ==================== HELPER METHODS ====================

    /**
     * Format play time as string
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

    /**
     * Get HP display string (e.g., "2/3")
     */
    public String getHpDisplay() {
        return playerHp + "/" + maxHp;
    }

    /**
     * Get task progress display (e.g., "3/5")
     */
    public String getTaskProgress() {
        return tasksCompleted + "/" + totalTasks;
    }

    @Override
    public String toString() {
        if (isEmpty) {
            return "Slot " + slotId + ": [EMPTY]";
        }
        return String.format("Slot %d: %s | HP: %s | Tasks: %s | Time: %s",
            slotId, currentMap, getHpDisplay(), getTaskProgress(), getFormattedPlayTime());
    }
}

