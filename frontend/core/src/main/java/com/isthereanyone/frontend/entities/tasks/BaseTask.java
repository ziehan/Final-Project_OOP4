package com.isthereanyone.frontend.entities.tasks;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Import Baru
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2; // Import Baru
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.observer.EventManager;

public abstract class BaseTask {
    protected Rectangle bounds;
    public boolean isCompleted = false;
    protected Color debugColor = Color.BLUE;

    // Tambahan untuk Gambar/Sprite
    protected TextureRegion texture;
    protected String type = "Generic"; // Default type

    public BaseTask(float x, float y) {
        this.bounds = new Rectangle(x, y, 32, 32);
    }

    // Method Render untuk GAMBAR (SpriteBatch) - Dipanggil di RoamingState
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.setColor(Color.WHITE);
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    // Method Render Debug (Kotak Biru) - Opsional
    public void render(ShapeRenderer shapeRenderer) {
        if (!isCompleted) shapeRenderer.setColor(debugColor);
        else shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public final void interact(Player player) {
        if (isCompleted) return;

        // Cek jarak/overlap manual jika perlu, atau panggil logic
        if (bounds.overlaps(player.getBoundingRectangle())) {
            startTask();
            executeLogic();
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

    public Rectangle getBounds() { return bounds; }

    // --- METHOD BARU YANG HILANG (PENYEBAB ERROR) ---
    public Vector2 getPosition() {
        return new Vector2(bounds.x, bounds.y);
    }

    public String getType() {
        return type;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }
    // ------------------------------------------------

    public abstract boolean updateMinigame(float delta, Viewport viewport, Player player);
    public abstract void renderMinigame(SpriteBatch batch, ShapeRenderer shape, Viewport viewport);
}
