package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.managers.ScreenManager;

public class MainMenuScreen extends BaseScreen {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private GlyphLayout glyphLayout;
    private float buttonPulse = 0f;

    private static final float W = GameConfig.VIEWPORT_WIDTH;
    private static final float H = GameConfig.VIEWPORT_HEIGHT;
    private static final float CX = W / 2f;
    private static final float CY = H / 2f;

    private static final float TITLE_W = 350f;
    private static final float TITLE_H = 70f;
    private static final float TITLE_X = CX - (TITLE_W / 2f);
    private static final float TITLE_Y = H - 100f;

    private static final float BTN_W = 160f;
    private static final float BTN_H = 40f;
    private static final float BTN_X = CX - (BTN_W / 2f);
    private static final float BTN_Y = 120f;

    private static final Color COLOR_TOP_BAR = new Color(0.7f, 0.15f, 0.15f, 1f);
    private static final Color COLOR_PANEL_BG = new Color(0.15f, 0.08f, 0.08f, 0.9f);
    private static final Color COLOR_BTN = new Color(0.6f, 0.15f, 0.15f, 0.8f);
    private static final Color COLOR_BORDER = new Color(0.8f, 0.2f, 0.2f, 1f);
    private static final Color COLOR_DECORATION = new Color(0.9f, 0.2f, 0.2f, 0.5f);
    private static final Color COLOR_SUBTITLE = new Color(1f, 0.6f, 0.6f, 1f);
    private static final Color COLOR_FOOTER = new Color(0.8f, 0.4f, 0.4f, 0.7f);

    public MainMenuScreen() {
        super();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(0.7f);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(1.5f);
        subtitleFont = new BitmapFont();
        subtitleFont.getData().setScale(0.6f);
        glyphLayout = new GlyphLayout();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);

        buttonPulse += delta;
        handleInput();
        renderUI();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            ScreenManager.getInstance().setScreen(new AuthenticationScreen());
        }
        handleMouseClicks();
    }

    private void handleMouseClicks() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);

            if (touch.x >= BTN_X && touch.x <= BTN_X + BTN_W &&
                touch.y >= BTN_Y && touch.y <= BTN_Y + BTN_H) {
                ScreenManager.getInstance().setScreen(new AuthenticationScreen());
            }
        }
    }

    private void renderUI() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < H; i++) {
            float t = i / H;
            shapeRenderer.setColor(0.05f + t * 0.05f, 0.02f + t * 0.02f, 0.02f + t * 0.03f, 1f);
            shapeRenderer.rect(0, i, W, 1);
        }

        shapeRenderer.setColor(COLOR_TOP_BAR);
        shapeRenderer.rect(0, H - 15f, W, 15f);

        shapeRenderer.setColor(COLOR_PANEL_BG);
        shapeRenderer.rect(TITLE_X, TITLE_Y, TITLE_W, TITLE_H);

        float pulse = (float) Math.sin(buttonPulse * 2.5f) * 0.15f + 1f;
        shapeRenderer.setColor(0.6f * pulse, 0.15f * pulse, 0.15f * pulse, 0.85f);
        shapeRenderer.rect(BTN_X, BTN_Y, BTN_W, BTN_H);

        shapeRenderer.setColor(COLOR_DECORATION);
        shapeRenderer.rect(30f, 80f, 12f, 12f);
        shapeRenderer.rect(W - 42f, 80f, 12f, 12f);
        shapeRenderer.rect(25f, 20f, 10f, 10f);
        shapeRenderer.rect(W - 35f, 20f, 10f, 10f);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_BORDER);
        shapeRenderer.rect(TITLE_X, TITLE_Y, TITLE_W, TITLE_H);
        shapeRenderer.rect(BTN_X, BTN_Y, BTN_W, BTN_H);
        shapeRenderer.end();

        spriteBatch.begin();

        String title = "IS THERE ANYONE?";
        titleFont.setColor(Color.WHITE);
        glyphLayout.setText(titleFont, title);
        titleFont.draw(spriteBatch, title, CX - glyphLayout.width / 2f, TITLE_Y + TITLE_H - 18f);

        String subtitle = "A Journey Into Darkness";
        subtitleFont.setColor(COLOR_SUBTITLE);
        glyphLayout.setText(subtitleFont, subtitle);
        subtitleFont.draw(spriteBatch, subtitle, CX - glyphLayout.width / 2f, TITLE_Y + 22f);

        String btnText = "START GAME";
        font.setColor(Color.WHITE);
        glyphLayout.setText(font, btnText);
        font.draw(spriteBatch, btnText, CX - glyphLayout.width / 2f, BTN_Y + BTN_H / 2f + glyphLayout.height / 2f);

        String instr = "Press ENTER or click to start";
        font.setColor(new Color(0.8f, 0.6f, 0.6f, 1f));
        font.getData().setScale(0.5f);
        glyphLayout.setText(font, instr);
        font.draw(spriteBatch, instr, CX - glyphLayout.width / 2f, 45f);

        String footer = "A Horror Adventure Game";
        font.setColor(COLOR_FOOTER);
        glyphLayout.setText(font, footer);
        font.draw(spriteBatch, footer, CX - glyphLayout.width / 2f, 75f);
        font.getData().setScale(0.7f);

        font.setColor(0.5f, 0.4f, 0.4f, 0.6f);
        font.getData().setScale(0.4f);
        font.draw(spriteBatch, "v1.0", W - 25f, 12f);
        font.getData().setScale(0.7f);

        spriteBatch.end();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
    }
}
