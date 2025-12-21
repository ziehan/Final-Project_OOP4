package com.isthereanyone.backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class GameSaveId implements Serializable {

    private String userId;
    private Integer slotId;

    public GameSaveId() {}

    public GameSaveId(String userId, Integer slotId) {
        this.userId = userId;
        this.slotId = slotId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSaveId that = (GameSaveId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(slotId, that.slotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, slotId);
    }
}

