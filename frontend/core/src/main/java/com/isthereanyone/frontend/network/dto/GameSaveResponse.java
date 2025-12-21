package com.isthereanyone.frontend.network.dto;

import java.util.Map;

/**
 * DTO untuk response data game save dari backend
 */
public class GameSaveResponse {
    private String userId;
    private Integer slotId;
    private Map<String, Object> saveData;
    private String lastUpdated;

    public GameSaveResponse() {}

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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

