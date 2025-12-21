package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.observer.EventManager;

public class WinScreen extends BaseScreen {
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont subFont;

    public WinScreen() {
        super();
        batch = new SpriteBatch();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Horror.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 60;
        params.color = Color.GREEN;
        params.borderWidth = 3;
        params.borderColor = Color.BLACK;
        titleFont = generator.generateFont(params);

        params.size = 20;
        params.color = Color.WHITE;
        params.borderWidth = 1;
        subFont = generator.generateFont(params);

        generator.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        GlyphLayout layout = new GlyphLayout(titleFont, "YOU ESCAPED!");
        titleFont.draw(batch, layout,
            (GameConfig.VIEWPORT_WIDTH - layout.width) / 2,
            (GameConfig.VIEWPORT_HEIGHT / 2) + 100);

        layout.setText(subFont, "Press ENTER to Play Again");
        subFont.draw(batch, layout,
            (GameConfig.VIEWPORT_WIDTH - layout.width) / 2,
            (GameConfig.VIEWPORT_HEIGHT / 2));

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            EventManager.getInstance();
            ScreenManager.getInstance().setScreen(new SaveSlotScreen());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        subFont.dispose();
    }
}
