package com.isthereanyone.frontend.network;

class AuthResponse {
    public boolean success;
    public String message;
    public String token;
    public UserData user;

    public AuthResponse() {}
}

class UserData {
    public String username;
    public String email;
    public long createdAt;

    public UserData() {}
}

class SaveSlotRequest {
    public int slotNumber;
    public SaveSlotData data;

    public SaveSlotRequest(int slotNumber, SaveSlotData data) {
        this.slotNumber = slotNumber;
        this.data = data;
    }
}

class SaveSlotData {
    public int playerLevel;
    public int playerHP;
    public float playerX;
    public float playerY;
    public long lastSavedTime;
    public String characterName;

    public SaveSlotData() {}

    public SaveSlotData(int playerLevel, int playerHP, float playerX, float playerY,
                        long lastSavedTime, String characterName) {
        this.playerLevel = playerLevel;
        this.playerHP = playerHP;
        this.playerX = playerX;
        this.playerY = playerY;
        this.lastSavedTime = lastSavedTime;
        this.characterName = characterName;
    }
}

class SaveSlotsResponse {
    public boolean success;
    public SaveSlotResponse[] slots;

    public SaveSlotsResponse() {}
}

class SaveSlotResponse {
    public int slotNumber;
    public boolean hasData;
    public SaveSlotData data;

    public SaveSlotResponse() {}
}
