package com.isthereanyone.frontend.observer;

public interface GameObserver {
    void onTaskCompleted(int totalTaskFinished);
    void onAllTasksCompleted();
    void onSoundEmitted(float x, float y, float radius);
}
