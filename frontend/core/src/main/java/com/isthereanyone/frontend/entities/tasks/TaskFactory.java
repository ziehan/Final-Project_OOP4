package com.isthereanyone.frontend.entities.tasks;

public class TaskFactory {
    public static BaseTask createTask(String type, float x, float y) {
        if (type.equalsIgnoreCase("STONE")) {
            return new StoneRingTask(x, y);
        } else if (type.equalsIgnoreCase("GENERATOR")) {
            return new GeneratorTask(x, y);
        } else if (type.equalsIgnoreCase("RITUAL")) {
            return new RitualTask(x, y);
        }
        return null;
    }
}
