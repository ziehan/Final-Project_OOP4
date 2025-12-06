package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.entities.Player;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GameHUD {
    private Viewport viewport;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private BitmapFont promptFont;
    private GlyphLayout layout;


    public GameHUD(SpriteBatch batch) {
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();
        this.font = new BitmapFont();
        this.font.getData().setScale(1.5f);
        this.layout = new GlyphLayout();

        OrthographicCamera camera = new OrthographicCamera();
        this.viewport = new FitViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);
        this.viewport.update((int)GameConfig.VIEWPORT_WIDTH, (int)GameConfig.VIEWPORT_HEIGHT, true);

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
            promptFont.draw(batch, promptText,
                (GameConfig.VIEWPORT_WIDTH / 2) - 40,
                100);
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(10, GameConfig.VIEWPORT_HEIGHT - 30, 104, 14);

        if (player.currentStamina > 30) {
            shapeRenderer.setColor(0f, 0.8f, 0f, 1f);
        } else {
            shapeRenderer.setColor(0.8f, 0f, 0f, 1f);
        }

        float barWidth = (player.currentStamina / player.maxStamina) * 100f;
        shapeRenderer.rect(12, GameConfig.VIEWPORT_HEIGHT - 28, barWidth, 10);

        shapeRenderer.end();
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
