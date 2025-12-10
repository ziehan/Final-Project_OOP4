package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.items.ItemType;

import java.util.List;

public class GameHUD {
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont promptFont;

    public GameHUD(SpriteBatch batch) {
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();

        OrthographicCamera camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Horror.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 24;
        parameter.color = Color.WHITE;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2;
        font = generator.generateFont(parameter);

        parameter.size = 18;
        parameter.color = Color.YELLOW;
        promptFont = generator.generateFont(parameter);

        generator.dispose();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void render(Player player, int finishedTasks, int totalTasks, String promptText) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        String taskText = "Ritual: " + finishedTasks + " / " + totalTasks;
        font.draw(batch, taskText, GameConfig.VIEWPORT_WIDTH - 200, GameConfig.VIEWPORT_HEIGHT - 20);

        if (promptText != null) {
            promptFont.draw(batch, promptText, (GameConfig.VIEWPORT_WIDTH / 2f) - 60, 150);
        }
        batch.end();

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(20, 20, 150, 15);

        if (player.currentStamina > 30) shapeRenderer.setColor(0f, 0.8f, 0f, 0.9f);
        else shapeRenderer.setColor(0.8f, 0.8f, 0f, 0.9f);

        float staminaWidth = (player.currentStamina / player.maxStamina) * 150f;
        shapeRenderer.rect(20, 20, staminaWidth, 15);

        float heartSize = 20;
        float heartY = 45;

        for (int i = 0; i < 2; i++) {
            if (i < player.health) {
                shapeRenderer.setColor(0.8f, 0f, 0f, 1f);
            } else {
                shapeRenderer.setColor(0.3f, 0f, 0f, 0.5f);
            }
            shapeRenderer.rect(20 + (i * (heartSize + 5)), heartY, heartSize, heartSize);
        }

        renderInventorySlots(player);

        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }

    private void renderInventorySlots(Player player) {
        float slotSize = 32;
        float gap = 10;
        int maxSlots = 3;

        float totalWidth = (maxSlots * slotSize) + ((maxSlots - 1) * gap);
        float startX = (GameConfig.VIEWPORT_WIDTH - totalWidth) / 2;
        float startY = 20;

        List<ItemType> items = player.inventory.getItems();
        int selectedIndex = player.inventory.getSelectedSlot();

        for (int i = 0; i < maxSlots; i++) {
            float x = startX + (i * (slotSize + gap));

            if (i == selectedIndex) {
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.rect(x - 2, startY - 2, slotSize + 4, slotSize + 4);
            }

            shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.8f);
            shapeRenderer.rect(x, startY, slotSize, slotSize);

            if (i < items.size()) {
                ItemType item = items.get(i);
                switch (item) {
                    case CANDLE: shapeRenderer.setColor(Color.ORANGE); break;
                    case DOLL: shapeRenderer.setColor(Color.MAGENTA); break;
                    case DAGGER: shapeRenderer.setColor(Color.LIGHT_GRAY); break;
                    case BOWL: shapeRenderer.setColor(Color.BROWN); break;
                    case FLOWER: shapeRenderer.setColor(Color.PINK); break;
                    default: shapeRenderer.setColor(Color.WHITE);
                }
                shapeRenderer.rect(x + 6, startY + 6, slotSize - 12, slotSize - 12);
            }
        }
    }

    public void dispose() {
        font.dispose();
        promptFont.dispose();
        shapeRenderer.dispose();
    }
}
