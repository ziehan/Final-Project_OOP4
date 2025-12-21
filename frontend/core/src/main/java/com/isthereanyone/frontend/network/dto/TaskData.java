package com.isthereanyone.frontend.network.dto;

/**
 * Model class untuk data Task
 * Digunakan untuk save/load status task
 */
public class TaskData {
    private String taskId;
    private String taskName;
    private String status; // "not_started", "in_progress", "completed"
    private int progress;
    private int maxProgress;
    private String location;
    private boolean isRequired;

    public TaskData() {}

    public TaskData(String taskId, String status) {
        this.taskId = taskId;
        this.status = status;
    }

    public TaskData(String taskId, String taskName, String status, int progress, int maxProgress) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.status = status;
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getMaxProgress() { return maxProgress; }
    public void setMaxProgress(int maxProgress) { this.maxProgress = maxProgress; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean required) { isRequired = required; }

    // Helper methods
    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isInProgress() {
        return "in_progress".equals(status);
    }

    public float getProgressPercentage() {
        if (maxProgress == 0) return 0;
        return (float) progress / maxProgress * 100;
    }
}

