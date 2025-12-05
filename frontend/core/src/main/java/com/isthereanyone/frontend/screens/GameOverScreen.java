package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.managers.ScreenManager;

public class GameOverScreen extends BaseScreen {
    private SpriteBatch batch;
    private BitmapFont font;

    public GameOverScreen() {
        super();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "YOU DIED", GameConfig.VIEWPORT_WIDTH/2 - 50, GameConfig.VIEWPORT_HEIGHT/2 + 20);
        font.draw(batch, "Press ENTER to Restart", GameConfig.VIEWPORT_WIDTH/2 - 100, GameConfig.VIEWPORT_HEIGHT/2 - 20);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ScreenManager.getInstance().setScreen(new MainMenuScreen());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}

