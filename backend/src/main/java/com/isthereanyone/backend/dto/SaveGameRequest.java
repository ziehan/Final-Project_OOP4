package com.isthereanyone.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * DTO untuk request save game
 */
public class SaveGameRequest {

    @NotBlank(message = "User ID tidak boleh kosong")
    private String userId;

    @NotNull(message = "Slot ID tidak boleh kosong")
    @Min(value = 1, message = "Slot ID minimal 1")
    @Max(value = 3, message = "Slot ID maksimal 3")
    private Integer slotId;

    @NotNull(message = "Save data tidak boleh kosong")
    private Map<String, Object> saveData;

    // Constructor
    public SaveGameRequest() {}

    public SaveGameRequest(String userId, Integer slotId, Map<String, Object> saveData) {
        this.userId = userId;
        this.slotId = slotId;
        this.saveData = saveData;
    }

    // Getters and Setters
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

