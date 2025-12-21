package com.isthereanyone.backend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class GameSaveResponse {

    private String userId;
    private Integer slotId;
    private Map<String, Object> saveData;
    private LocalDateTime lastUpdated;

    public GameSaveResponse() {}

    public GameSaveResponse(String userId, Integer slotId, Map<String, Object> saveData, LocalDateTime lastUpdated) {
        this.userId = userId;
        this.slotId = slotId;
        this.saveData = saveData;
        this.lastUpdated = lastUpdated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getSlotId() {
        return slotId;
    }

    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }

    public Map<String, Object> getSaveData() {
        return saveData;
    }

    public void setSaveData(Map<String, Object> saveData) {
        this.saveData = saveData;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

