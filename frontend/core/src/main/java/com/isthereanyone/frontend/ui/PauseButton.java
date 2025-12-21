package com.isthereanyone.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;

public class PauseButton {
    private ShapeRenderer shapeRenderer;
    private Viewport viewport;

    private static final float BTN_X = 10f;
    private static final float BTN_Y = GameConfig.VIEWPORT_HEIGHT - 35f;
    private static final float BTN_SIZE = 25f;

    private boolean isHovered = false;
    private Runnable onClickCallback;

    public PauseButton(Viewport viewport) {
        this.viewport = viewport;
        shapeRenderer = new ShapeRenderer();
    }

    public void setOnClickCallback(Runnable callback) {
        this.onClickCallback = callback;
    }

    public void update() {
        Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touch);

        isHovered = isInArea(touch.x, touch.y, BTN_X, BTN_Y, BTN_SIZE, BTN_SIZE);

        if (isHovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (onClickCallback != null) {
                onClickCallback.run();
            }
        }
    }

    private boolean isInArea(float x, float y, float ax, float ay, float aw, float ah) {
        return x >= ax && x <= ax + aw && y >= ay && y <= ay + ah;
    }

    public void render() {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        if (isHovered) {
            shapeRenderer.setColor(0.6f, 0.2f, 0.2f, 0.95f);
        } else {
            shapeRenderer.setColor(0.4f, 0.15f, 0.15f, 0.85f);
        }
        shapeRenderer.rect(BTN_X, BTN_Y, BTN_SIZE, BTN_SIZE);

        shapeRenderer.setColor(Color.WHITE);
        float barWidth = 4f;
        float barHeight = 14f;
        float barY = BTN_Y + (BTN_SIZE - barHeight) / 2f;
        float bar1X = BTN_X + 7f;
        float bar2X = BTN_X + BTN_SIZE - 7f - barWidth;
        shapeRenderer.rect(bar1X, barY, barWidth, barHeight);
        shapeRenderer.rect(bar2X, barY, barWidth, barHeight);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(isHovered ? new Color(1f, 0.4f, 0.4f, 1f) : new Color(0.8f, 0.3f, 0.3f, 1f));
        shapeRenderer.rect(BTN_X, BTN_Y, BTN_SIZE, BTN_SIZE);
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
