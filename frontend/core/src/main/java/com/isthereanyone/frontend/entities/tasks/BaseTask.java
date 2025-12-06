package com.isthereanyone.frontend.entities.tasks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.observer.EventManager;

public abstract class BaseTask {
    protected Rectangle bounds;
    public boolean isCompleted = false;
    protected Color debugColor = Color.BLUE;

    public BaseTask(float x, float y) {
        this.bounds = new Rectangle(x, y, 32, 32);
    }

    public final void interact(Player player) {
        if (isCompleted) return;
        if (bounds.overlaps(new Rectangle(player.position.x, player.position.y, 32, 32))) {
            startTask();
            executeLogic();
            completeTask();
        }
    }

    private void startTask() { System.out.println("Starting task..."); }

    protected abstract void executeLogic();

    protected void completeTask() {
        isCompleted = true;
        debugColor = Color.GREEN;
        System.out.println("TASK DONE!");
        EventManager.getInstance().notifyTaskCompleted(1);
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(debugColor);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}

