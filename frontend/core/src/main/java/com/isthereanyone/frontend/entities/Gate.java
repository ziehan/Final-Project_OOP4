package com.isthereanyone.frontend.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.isthereanyone.frontend.observer.EventManager;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.observer.GameObserver;
import com.isthereanyone.frontend.screens.WinScreen;

public class Gate implements GameObserver {
    private Rectangle bounds;
    private boolean isLocked = true;

    public Gate(float x, float y){
        this.bounds = new Rectangle(x, y, 64, 64);
        EventManager.getInstance().addObserver(this);
    }

    @Override
    public void onTaskCompleted(int totalTaskFinished) {}

    @Override
    public void onAllTasksCompleted() {
        isLocked = false;
        System.out.println("GATE: *KLAK* Suara rantai jatuh... Gerbang terbuka!");
    }

    @Override
    public void onSoundEmitted(float x, float y, float radius) {

    }

    public void interact() {
        if (isLocked) {
            System.out.println("Gerbang terkunci rapat. Selesaikan ritual dulu.");
        } else {
            System.out.println("KABUR DARI DESA!");
            ScreenManager.getInstance().setScreen(new WinScreen());
        }
    }

    public void render(ShapeRenderer shape) {
        if (isLocked) {
            shape.setColor(Color.RED);
        } else {
            shape.setColor(Color.CYAN);
        }
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isLocked() {
        return isLocked;
    }
}
