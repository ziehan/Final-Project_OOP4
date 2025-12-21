package com.isthereanyone.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.managers.AudioManager;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.screens.SaveLoadScreen;

public class PauseMenu {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout glyphLayout;
    private Viewport viewport;

    private boolean isVisible = false;
    private boolean showSettings = false;
    private int selectedOption = 0;
    private boolean canProcessEsc = false;

    private float masterVolume = 0.8f;
    private float musicVolume = 0.5f; // Will be synced from AudioManager
    private float sfxVolume = 0.8f;

    private static final float W = GameConfig.VIEWPORT_WIDTH;
    private static final float H = GameConfig.VIEWPORT_HEIGHT;
    private static final float CX = W / 2f;
    private static final float CY = H / 2f;

    private static final float POPUP_W = 220f;
    private static final float POPUP_H = 200f;
    private static final float POPUP_X = CX - POPUP_W / 2f;
    private static final float POPUP_Y = CY - POPUP_H / 2f;

    private static final float BTN_W = 160f;
    private static final float BTN_H = 30f;
    private static final float BTN_X = CX - BTN_W / 2f;
    private static final float BTN_GAP = 38f;
    private static final float BTN_START_Y = POPUP_Y + POPUP_H - 60f;

    private static final float SETTINGS_W = 300f;
    private static final float SETTINGS_H = 240f;
    private static final float SETTINGS_X = CX - SETTINGS_W / 2f;
    private static final float SETTINGS_Y = CY - SETTINGS_H / 2f;

    private static final float SLIDER_W = 120f;
    private static final float SLIDER_H = 10f;
    private static final float SLIDER_GAP = 45f;

    private static final String[] MENU_OPTIONS = {"RESUME", "SAVE PROGRESS", "SETTINGS", "EXIT TO MENU"};
    private static final String[] VOLUME_LABELS = {"Master Volume", "Music Volume", "SFX Volume"};
    private int settingsSelectedOption = 0;

    private Runnable onSaveCallback;

    private static final Color COLOR_BG = new Color(0.08f, 0.05f, 0.05f, 0.98f);
    private static final Color COLOR_BTN = new Color(0.3f, 0.08f, 0.08f, 1f);
    private static final Color COLOR_BTN_SELECTED = new Color(0.55f, 0.12f, 0.12f, 1f);
    private static final Color COLOR_BORDER = new Color(0.7f, 0.15f, 0.15f, 1f);
    private static final Color COLOR_BORDER_SELECTED = new Color(1f, 0.25f, 0.25f, 1f);
    private static final Color COLOR_SLIDER_BG = new Color(0.15f, 0.08f, 0.08f, 1f);
    private static final Color COLOR_SLIDER_FILL = new Color(0.8f, 0.2f, 0.2f, 1f);

    public PauseMenu(Viewport viewport) {
        this.viewport = viewport;
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(0.55f);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(0.9f);
        glyphLayout = new GlyphLayout();

        // Sync music volume from AudioManager
        musicVolume = AudioManager.getInstance().getMusicVolume();
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void show() {
        isVisible = true;
        showSettings = false;
        selectedOption = 0;
        canProcessEsc = false;
    }

    public void hide() {
        isVisible = false;
        showSettings = false;
    }

    public void toggle() {
        if (isVisible) hide();
        else show();
    }

    public void update() {
        if (!isVisible) return;

        if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            canProcessEsc = true;
        }

        if (showSettings) {
            updateSettings();
        } else {
            updateMainMenu();
        }
    }

    private void updateMainMenu() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedOption = (selectedOption - 1 + MENU_OPTIONS.length) % MENU_OPTIONS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedOption = (selectedOption + 1) % MENU_OPTIONS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executeOption(selectedOption);
        }

        if (canProcessEsc && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            hide();
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);

            for (int i = 0; i < MENU_OPTIONS.length; i++) {
                float btnY = BTN_START_Y - i * BTN_GAP;
                if (isInArea(touch.x, touch.y, BTN_X, btnY, BTN_W, BTN_H)) {
                    executeOption(i);
                    break;
                }
            }
        }
    }

    private void updateSettings() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            settingsSelectedOption = (settingsSelectedOption - 1 + 4) % 4;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            settingsSelectedOption = (settingsSelectedOption + 1) % 4;
        }

        if (settingsSelectedOption < 3) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                adjustVolume(settingsSelectedOption, -0.1f);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                adjustVolume(settingsSelectedOption, 0.1f);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            if (settingsSelectedOption == 3) {
                showSettings = false;
            }
        }

        if (canProcessEsc && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            showSettings = false;
        }

        if (Gdx.input.isTouched()) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);

            float sliderX = SETTINGS_X + SETTINGS_W - SLIDER_W - 25f;
            for (int i = 0; i < 3; i++) {
                float sliderY = SETTINGS_Y + SETTINGS_H - 80f - i * SLIDER_GAP;
                if (isInArea(touch.x, touch.y, sliderX - 5f, sliderY - 5f, SLIDER_W + 10f, SLIDER_H + 10f)) {
                    float value = (touch.x - sliderX) / SLIDER_W;
                    setVolume(i, Math.max(0f, Math.min(1f, value)));
                }
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);

            float backY = SETTINGS_Y + 35f;
            if (isInArea(touch.x, touch.y, CX - 50f, backY, 100f, 28f)) {
                showSettings = false;
            }
        }
    }

    private void executeOption(int option) {
        switch (option) {
            case 0: hide(); break;
            case 1: if (onSaveCallback != null) onSaveCallback.run(); break;
            case 2: showSettings = true; settingsSelectedOption = 0; break;
            case 3: hide(); ScreenManager.getInstance().setScreen(new SaveLoadScreen()); break;
        }
    }

    private void adjustVolume(int index, float delta) {
        switch (index) {
            case 0: masterVolume = Math.max(0f, Math.min(1f, masterVolume + delta)); break;
            case 1:
                musicVolume = Math.max(0f, Math.min(1f, musicVolume + delta));
                AudioManager.getInstance().setMusicVolume(musicVolume);
                break;
            case 2: sfxVolume = Math.max(0f, Math.min(1f, sfxVolume + delta)); break;
        }
    }

    private void setVolume(int index, float value) {
        switch (index) {
            case 0: masterVolume = value; break;
            case 1:
                musicVolume = value;
                AudioManager.getInstance().setMusicVolume(musicVolume);
                break;
            case 2: sfxVolume = value; break;
        }
    }

    private float getVolume(int index) {
        switch (index) {
            case 0: return masterVolume;
            case 1: return musicVolume;
            case 2: return sfxVolume;
            default: return 0f;
        }
    }

    public float getMasterVolume() { return masterVolume; }
    public float getMusicVolume() { return musicVolume; }
    public float getSfxVolume() { return sfxVolume; }

    private boolean isInArea(float x, float y, float ax, float ay, float aw, float ah) {
        return x >= ax && x <= ax + aw && y >= ay && y <= ay + ah;
    }

    public void render() {
        if (!isVisible) return;

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.75f);
        shapeRenderer.rect(0, 0, W, H);
        shapeRenderer.end();

        if (showSettings) {
            renderSettings();
        } else {
            renderMainMenu();
        }
    }

    private void renderMainMenu() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.15f, 0f, 0f, 0.5f);
        shapeRenderer.rect(POPUP_X + 5, POPUP_Y - 5, POPUP_W, POPUP_H);

        shapeRenderer.setColor(COLOR_BG);
        shapeRenderer.rect(POPUP_X, POPUP_Y, POPUP_W, POPUP_H);

        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            float btnY = BTN_START_Y - i * BTN_GAP;
            shapeRenderer.setColor(selectedOption == i ? COLOR_BTN_SELECTED : COLOR_BTN);
            shapeRenderer.rect(BTN_X, btnY, BTN_W, BTN_H);
        }

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_BORDER);
        shapeRenderer.rect(POPUP_X, POPUP_Y, POPUP_W, POPUP_H);

        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            float btnY = BTN_START_Y - i * BTN_GAP;
            shapeRenderer.setColor(selectedOption == i ? COLOR_BORDER_SELECTED : COLOR_BORDER);
            shapeRenderer.rect(BTN_X, btnY, BTN_W, BTN_H);
        }
        shapeRenderer.end();

        spriteBatch.begin();

        titleFont.setColor(1f, 0.3f, 0.3f, 1f);
        glyphLayout.setText(titleFont, "PAUSED");
        titleFont.draw(spriteBatch, "PAUSED", CX - glyphLayout.width / 2f, POPUP_Y + POPUP_H - 18f);

        for (int i = 0; i < MENU_OPTIONS.length; i++) {
            float btnY = BTN_START_Y - i * BTN_GAP;
            font.setColor(selectedOption == i ? Color.WHITE : new Color(0.8f, 0.6f, 0.6f, 1f));
            glyphLayout.setText(font, MENU_OPTIONS[i]);
            font.draw(spriteBatch, MENU_OPTIONS[i], CX - glyphLayout.width / 2f, btnY + BTN_H / 2f + glyphLayout.height / 2f);
        }

        font.setColor(0.6f, 0.4f, 0.4f, 1f);
        font.getData().setScale(0.4f);
        String instr = "W/S: navigate | ENTER: select | ESC: resume";
        glyphLayout.setText(font, instr);
        font.draw(spriteBatch, instr, CX - glyphLayout.width / 2f, POPUP_Y + 12f);
        font.getData().setScale(0.55f);

        spriteBatch.end();
    }

    private void renderSettings() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.15f, 0f, 0f, 0.5f);
        shapeRenderer.rect(SETTINGS_X + 5, SETTINGS_Y - 5, SETTINGS_W, SETTINGS_H);

        shapeRenderer.setColor(COLOR_BG);
        shapeRenderer.rect(SETTINGS_X, SETTINGS_Y, SETTINGS_W, SETTINGS_H);

        float sliderX = SETTINGS_X + SETTINGS_W - SLIDER_W - 25f;
        for (int i = 0; i < 3; i++) {
            float sliderY = SETTINGS_Y + SETTINGS_H - 80f - i * SLIDER_GAP;

            shapeRenderer.setColor(COLOR_SLIDER_BG);
            shapeRenderer.rect(sliderX, sliderY, SLIDER_W, SLIDER_H);

            float value = getVolume(i);
            shapeRenderer.setColor(COLOR_SLIDER_FILL);
            shapeRenderer.rect(sliderX, sliderY, SLIDER_W * value, SLIDER_H);

            shapeRenderer.setColor(Color.WHITE);
            float knobX = sliderX + SLIDER_W * value - 3f;
            shapeRenderer.rect(knobX, sliderY - 3f, 6f, SLIDER_H + 6f);
        }

        float backY = SETTINGS_Y + 35f;
        shapeRenderer.setColor(settingsSelectedOption == 3 ? COLOR_BTN_SELECTED : COLOR_BTN);
        shapeRenderer.rect(CX - 50f, backY, 100f, 28f);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_BORDER);
        shapeRenderer.rect(SETTINGS_X, SETTINGS_Y, SETTINGS_W, SETTINGS_H);

        for (int i = 0; i < 3; i++) {
            float sliderY = SETTINGS_Y + SETTINGS_H - 80f - i * SLIDER_GAP;
            shapeRenderer.setColor(settingsSelectedOption == i ? COLOR_BORDER_SELECTED : COLOR_BORDER);
            shapeRenderer.rect(sliderX, sliderY, SLIDER_W, SLIDER_H);
        }

        shapeRenderer.setColor(settingsSelectedOption == 3 ? COLOR_BORDER_SELECTED : COLOR_BORDER);
        shapeRenderer.rect(CX - 50f, backY, 100f, 28f);

        shapeRenderer.end();

        spriteBatch.begin();

        titleFont.setColor(1f, 0.3f, 0.3f, 1f);
        glyphLayout.setText(titleFont, "SETTINGS");
        titleFont.draw(spriteBatch, "SETTINGS", CX - glyphLayout.width / 2f, SETTINGS_Y + SETTINGS_H - 20f);

        for (int i = 0; i < 3; i++) {
            float labelY = SETTINGS_Y + SETTINGS_H - 73f - i * SLIDER_GAP;
            font.setColor(settingsSelectedOption == i ? Color.WHITE : new Color(0.8f, 0.6f, 0.6f, 1f));
            font.draw(spriteBatch, VOLUME_LABELS[i], SETTINGS_X + 20f, labelY);

            int pct = (int)(getVolume(i) * 100);
            String pctText = pct + "%";
            glyphLayout.setText(font, pctText);
            font.draw(spriteBatch, pctText, sliderX - glyphLayout.width - 10f, labelY);
        }

        font.setColor(settingsSelectedOption == 3 ? Color.WHITE : new Color(0.8f, 0.6f, 0.6f, 1f));
        glyphLayout.setText(font, "BACK");
        font.draw(spriteBatch, "BACK", CX - glyphLayout.width / 2f, backY + 19f);

        font.setColor(0.6f, 0.4f, 0.4f, 1f);
        font.getData().setScale(0.4f);
        String instr = "A/D: adjust | W/S: navigate | ESC: back";
        glyphLayout.setText(font, instr);
        font.draw(spriteBatch, instr, CX - glyphLayout.width / 2f, SETTINGS_Y + 12f);
        font.getData().setScale(0.55f);

        spriteBatch.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
        titleFont.dispose();
    }
}
