package com.isthereanyone.frontend.managers;

import java.util.HashMap;
import java.util.Map;

public class SaveSlotManager {
    private static SaveSlotManager instance;
    private static final int TOTAL_SLOTS = 3;
    private int currentSlot = -1;
    private Map<Integer, SaveSlotData> slots = new HashMap<>();

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

    public boolean loadSavesForUser(String username) {
        System.out.println("[SAVE] Loading save slots for user: " + username);
        if (NetworkManager.getInstance().loadSaveSlots(username)) {
            System.out.println("[SAVE] Save slots loaded successfully");
            return true;
        }
        return false;
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

        if (NetworkManager.getInstance().saveGameData(
            AuthenticationManager.getInstance().getCurrentUsername(),
            currentSlot,
            slot)) {
            System.out.println("[SAVE] Game saved to slot " + currentSlot);
            return true;
        }
        return false;
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

    public static class SaveSlotData {
        private int slotNumber;
        private boolean hasData;
        private int playerLevel;
        private int playerHP;
        private float playerX;
        private float playerY;
        private long lastSavedTime;
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

        public String getCharacterName() { return characterName; }
        public void setCharacterName(String characterName) { this.characterName = characterName; }

        @Override
        public String toString() {
            if (!hasData) {
                return "Slot " + slotNumber + " - Empty";
            }
            return "Slot " + slotNumber + " - Level: " + playerLevel + ", HP: " + playerHP +
                ", Pos: (" + playerX + ", " + playerY + ")";
        }
    }
}
