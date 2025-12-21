package com.isthereanyone.backend.dto;

/**
 * DTO untuk data Task
 */
public class TaskData {
    private String taskId;
    private String taskName;
    private String status; // "not_started", "in_progress", "completed"
    private Integer progress;
    private Integer maxProgress;
    private String location; // room/area where task is located
    private Boolean isRequired; // apakah task wajib untuk menang

    public TaskData() {}

    public TaskData(String taskId, String status) {
        this.taskId = taskId;
        this.status = status;
    }

    public TaskData(String taskId, String taskName, String status, Integer progress, Integer maxProgress) {
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

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public Integer getMaxProgress() { return maxProgress; }
    public void setMaxProgress(Integer maxProgress) { this.maxProgress = maxProgress; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    // Helper methods
    public boolean isCompleted() {
        return "completed".equals(status);
    }

    public boolean isInProgress() {
        return "in_progress".equals(status);
    }

    public float getProgressPercentage() {
        if (maxProgress == null || maxProgress == 0) return 0;
        if (progress == null) return 0;
        return (float) progress / maxProgress * 100;
    }
}

