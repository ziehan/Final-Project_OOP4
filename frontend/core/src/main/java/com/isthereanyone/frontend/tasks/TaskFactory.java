package com.isthereanyone.frontend.tasks;

public class TaskFactory {
<<<<<<< Updated upstream
=======
    public static BaseTask createTask(String type, float x, float y) {
        if (type.equalsIgnoreCase("WIRE")) {
            return new WireTask(x, y);
        } else if (type.equalsIgnoreCase("RITUAL")) {
            return new RitualTask(x, y);
        }

        System.out.println("Tipe task tidak dikenal: " + type);
        return null;
    }
>>>>>>> Stashed changes
}
