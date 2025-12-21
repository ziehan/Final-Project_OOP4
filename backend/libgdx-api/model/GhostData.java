package com.isthereanyone.api.model;

/**
 * Model class untuk data Ghost
 * Copy ke project frontend LibGDX
 */
public class GhostData {
    private float x;
    private float y;
    private String state; // "patrol", "chase", "idle", "returning"
    private String currentPatrolPoint;
    private int alertLevel; // 0-100

    public GhostData() {}

    public GhostData(float x, float y, String state) {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    // Getters and Setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCurrentPatrolPoint() { return currentPatrolPoint; }
    public void setCurrentPatrolPoint(String currentPatrolPoint) { this.currentPatrolPoint = currentPatrolPoint; }

    public int getAlertLevel() { return alertLevel; }
    public void setAlertLevel(int alertLevel) { this.alertLevel = alertLevel; }
}

