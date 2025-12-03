package com.isthereanyone.frontend.tasks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.isthereanyone.frontend.entities.Player;

public abstract class BaseTask {
    protected Rectangle bounds;
    protected boolean isCompleted = false;

    public BaseTask(float x, float y) {
        this.bounds = new Rectangle(x, y, 20, 20);
    }

    public void interact(Player player) {
        if (!isCompleted && bounds.contains(player.position)) {
            startTask();
            performLogic();
            completeTask();
        }
    }

    private void startTask() {
        System.out.println("Starting task...");
    }

    protected abstract void performLogic();

    private void completeTask() {
        isCompleted = true;
        System.out.println("Task Completed!");
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (isCompleted) shapeRenderer.setColor(Color.GREEN);
        else shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
