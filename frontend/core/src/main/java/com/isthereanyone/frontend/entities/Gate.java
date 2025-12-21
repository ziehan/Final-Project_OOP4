package com.isthereanyone.frontend.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.managers.GameWorld;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.observer.EventManager;
import com.isthereanyone.frontend.observer.GameObserver;
import com.isthereanyone.frontend.screens.WinScreen;

public class Gate implements GameObserver {
    private Rectangle bounds;
    private boolean isLocked = true;
    private TextureRegion region;

    public Gate(float x, float y) {
        this.bounds = new Rectangle(x, y, 32, 32);
        EventManager.getInstance().addObserver(this);
    }

    public void setTexture(TextureRegion region) {
        this.region = region;
        if (region != null) {
            this.bounds.width = region.getRegionWidth();
            this.bounds.height = region.getRegionHeight();
        }
    }

    public void update(GameWorld world) {
        boolean allDone = true;
        for (BaseTask task : world.tasks) {
            if (!task.isCompleted) {
                allDone = false;
                break;
            }
        }
        this.isLocked = !allDone;
    }

    @Override
    public void onTaskCompleted(int totalTaskFinished) {}

    @Override
    public void onAllTasksCompleted() {
        isLocked = false;
        System.out.println("GATE: *KLAK* Gerbang terbuka!");
    }

    @Override
    public void onSoundEmitted(float x, float y, float radius) {}

    public void interact() {
        if (isLocked) {
            System.out.println("Gerbang terkunci rapat.");
        } else {
            System.out.println("YOU WIN!");
            ScreenManager.getInstance().setScreen(new WinScreen());
        }
    }

    public void render(SpriteBatch batch) {
        if (region != null) {
            batch.draw(region, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
