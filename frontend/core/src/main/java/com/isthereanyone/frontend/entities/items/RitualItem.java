package com.isthereanyone.frontend.entities.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class RitualItem {
    private ItemType type;
    private Rectangle bounds;
    public boolean isCollected = false;

    public RitualItem(ItemType type, float x, float y) {
        this.type = type;
        this.bounds = new Rectangle(x, y, 20, 20);
    }

    public void render(SpriteBatch batch, ShapeRenderer shape) {
        if (isCollected) return;

        switch (type) {
            case CANDLE: shape.setColor(Color.ORANGE); break;
            case DOLL: shape.setColor(Color.BROWN); break;
            case DAGGER: shape.setColor(Color.GRAY); break;
            case BOWL: shape.setColor(Color.RED); break;
            case FLOWER: shape.setColor(Color.WHITE); break;
        }
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() { return bounds; }
    public ItemType getType() { return type; }
}
