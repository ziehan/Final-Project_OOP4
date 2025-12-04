package com.isthereanyone.frontend.observer;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static EventManager instance;
    private List<GameObserver> observers;

    private EventManager(){
        observers = new ArrayList<>();
    }

    public static EventManager getInstance(){
        if(instance == null){
            instance = new EventManager();
        }

        return instance;
    }

    public void addObserver(GameObserver observer){
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer){
        observers.remove(observer);
    }

    public void notifyTaskCompleted(int totalFinished){
        for(GameObserver observer : observers){
            observer.onTaskCompleted(totalFinished);
        }
    }
}
