package com.isthereanyone.frontend.entities.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RitualItem {
    private ItemType type;
    private Rectangle bounds;

    // --- FIELD TAMBAHAN (FIX ERROR) ---
    public boolean isCollected = false;
    // ----------------------------------

    private static Texture debugTexture;

    public RitualItem(ItemType type, float x, float y) {
        this.type = type;
        this.bounds = new Rectangle(x, y, 24, 24);

        if (debugTexture == null) {
            Pixmap pixmap = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.RED);
            pixmap.fill();
            debugTexture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    public Vector2 getPosition() {
        return new Vector2(bounds.x, bounds.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public ItemType getType() {
        return type;
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Cek status isCollected sebelum menggambar
        if (isCollected) return;

        if (batch != null && debugTexture != null) {
            batch.setColor(Color.WHITE);
            batch.draw(debugTexture, bounds.x, bounds.y);
        }
    }
}
