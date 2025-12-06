package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GameOverScreen extends BaseScreen {
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont subFont;

    public GameOverScreen() {
        super();
        batch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Horror.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 60;
        params.color = Color.RED;
        params.borderWidth = 3;
        titleFont = generator.generateFont(params);

        params.size = 20;
        params.color = Color.WHITE;
        params.borderWidth = 1;
        subFont = generator.generateFont(params);

        generator.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        GlyphLayout layout = new GlyphLayout(titleFont, "YOU DIED");
        titleFont.draw(batch, layout,
            (GameConfig.VIEWPORT_WIDTH - layout.width) / 2,
            (GameConfig.VIEWPORT_HEIGHT / 2) + 100);

        layout.setText(subFont, "Press ENTER to Restart");
        subFont.draw(batch, layout,
            (GameConfig.VIEWPORT_WIDTH - layout.width) / 2,
            (GameConfig.VIEWPORT_HEIGHT / 2));

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ScreenManager.getInstance().setScreen(new MainMenuScreen());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        subFont.dispose();
    }
}

