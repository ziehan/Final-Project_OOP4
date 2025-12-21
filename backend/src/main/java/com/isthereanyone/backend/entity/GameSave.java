package com.isthereanyone.backend.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity untuk menyimpan game save data
 * Menggunakan JSONB PostgreSQL untuk menyimpan data game yang kompleks
 */
@Entity
@Table(name = "game_saves")
@IdClass(GameSaveId.class)
public class GameSave {

    @Id
    @Column(name = "user_id", length = 50, nullable = false)
    @NotBlank(message = "User ID tidak boleh kosong")
    private String userId;

    @Id
    @Column(name = "slot_id", nullable = false)
    @Min(value = 1, message = "Slot ID minimal 1")
    @Max(value = 3, message = "Slot ID maksimal 3")
    @NotNull(message = "Slot ID tidak boleh kosong")
    private Integer slotId;

    @Type(JsonType.class)
    @Column(name = "save_data", columnDefinition = "jsonb", nullable = false)
    @NotNull(message = "Save data tidak boleh kosong")
    private Map<String, Object> saveData;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Constructor
    public GameSave() {}

    public GameSave(String userId, Integer slotId, Map<String, Object> saveData) {
        this.userId = userId;
        this.slotId = slotId;
        this.saveData = saveData;
        this.lastUpdated = LocalDateTime.now();
    }

    // Lifecycle callback - auto update timestamp
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
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

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "GameSave{" +
                "userId='" + userId + '\'' +
                ", slotId=" + slotId +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}

