package com.isthereanyone.frontend.tasks;

public class TaskFactory {
    public static BaseTask createTask(String type, float x, float y) {
        if (type.equalsIgnoreCase("SIMPLE")) {
            return new SimpleTask(x, y);
        }
        return null;
    }
}
