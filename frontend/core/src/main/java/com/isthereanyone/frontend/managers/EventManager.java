package com.isthereanyone.frontend.managers;

import com.isthereanyone.frontend.observer.GameObserver;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static EventManager instance;
    private List<GameObserver> observers;

    // Counter Global
    private int finishedTaskCount = 0;
    private final int TOTAL_TASKS_REQUIRED = 3;

    private EventManager() {
        observers = new ArrayList<>();
    }

    public static EventManager getInstance() {
        if (instance == null) instance = new EventManager();
        return instance;
    }

    public void reset() {
        finishedTaskCount = 0;
    }

    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    public void notifyTaskCompleted(int count) {
        finishedTaskCount += count;
        System.out.println("Event Manager: Progress " + finishedTaskCount + "/" + TOTAL_TASKS_REQUIRED);

        for (GameObserver observer : observers) {
            observer.onTaskCompleted(finishedTaskCount);

            if (finishedTaskCount >= TOTAL_TASKS_REQUIRED) {
                observer.onAllTasksCompleted();
            }
        }
    }

    public int getFinishedTaskCount() {
        return finishedTaskCount;
    }
}
