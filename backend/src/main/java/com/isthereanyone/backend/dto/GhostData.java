package com.isthereanyone.backend.dto;


/**
 * DTO untuk data Ghost
 */
public class GhostData {
    private Float x;
    private Float y;
    private String state; // "patrol", "chase", "idle", "returning"
    private String currentPatrolPoint;
    private Integer alertLevel; // 0-100

    public GhostData() {}

    public GhostData(Float x, Float y, String state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    // Getters and Setters
    public Float getX() { return x; }
    public void setX(Float x) { this.x = x; }

    public Float getY() { return y; }
    public void setY(Float y) { this.y = y; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCurrentPatrolPoint() { return currentPatrolPoint; }
    public void setCurrentPatrolPoint(String currentPatrolPoint) { this.currentPatrolPoint = currentPatrolPoint; }

    public Integer getAlertLevel() { return alertLevel; }
    public void setAlertLevel(Integer alertLevel) { this.alertLevel = alertLevel; }
}

