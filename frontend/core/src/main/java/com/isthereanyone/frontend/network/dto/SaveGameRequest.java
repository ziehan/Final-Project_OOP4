package com.isthereanyone.frontend.network.dto;

import java.util.Map;

/**
 * DTO untuk request simpan game ke backend
 */
public class SaveGameRequest {
    private String userId;
    private Integer slotId;
    private Map<String, Object> saveData;

    public SaveGameRequest() {}

    public SaveGameRequest(String userId, Integer slotId, Map<String, Object> saveData) {
        this.userId = userId;
        this.slotId = slotId;
        this.saveData = saveData;
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
}

