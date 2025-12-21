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
import com.isthereanyone.frontend.managers.AuthenticationManager;
import com.isthereanyone.frontend.managers.SaveSlotManager;
import com.isthereanyone.frontend.managers.ScreenManager;

public class SaveLoadScreen extends BaseScreen {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout glyphLayout;

    private int selectedSlot = 0;
    private SaveSlotManager saveSlotManager;
    private String username;
    private String actionMessage = "";
    private float actionMessageTimer = 0f;

    private static final float W = GameConfig.VIEWPORT_WIDTH;
    private static final float H = GameConfig.VIEWPORT_HEIGHT;
    private static final float CX = W / 2f;
    private static final float CY = H / 2f;

    private static final float SLOT_W = 180f;
    private static final float SLOT_H = 45f;
    private static final float SLOT_GAP = 55f;
    private static final float SLOT_X = CX - SLOT_W / 2f;
    private static final float SLOT_START_Y = 250f;

    private static final float BTN_W = 85f;
    private static final float BTN_H = 28f;
    private static final float BTN_GAP = 15f;
    private static final float BTN_Y = 30f;
    private static final float BTN1_X = CX - BTN_W - BTN_GAP / 2f;
    private static final float BTN2_X = CX + BTN_GAP / 2f;

    private static final Color COLOR_BG_TOP = new Color(0.05f, 0.02f, 0.02f, 1f);
    private static final Color COLOR_BG_BOTTOM = new Color(0.10f, 0.05f, 0.07f, 1f);
    private static final Color COLOR_HEADER = new Color(0.7f, 0.15f, 0.15f, 0.95f);
    private static final Color COLOR_PANEL_BG = new Color(0.15f, 0.08f, 0.08f, 0.9f);
    private static final Color COLOR_SLOT_SELECTED = new Color(0.6f, 0.15f, 0.15f, 0.9f);
    private static final Color COLOR_SLOT_UNSELECTED = new Color(0.12f, 0.06f, 0.06f, 0.9f);
    private static final Color COLOR_BORDER = new Color(0.8f, 0.2f, 0.2f, 1f);
    private static final Color COLOR_BTN1 = new Color(0.6f, 0.15f, 0.15f, 0.85f);
    private static final Color COLOR_BTN2 = new Color(0.5f, 0.12f, 0.12f, 0.85f);
    private static final Color COLOR_TEXT_SUB = new Color(1f, 0.6f, 0.6f, 1f);
    private static final Color COLOR_TEXT_DIM = new Color(0.8f, 0.6f, 0.6f, 0.9f);
    private static final Color COLOR_STATUS_HASDATA = new Color(0.9f, 0.2f, 0.2f, 1f);
    private static final Color COLOR_STATUS_EMPTY = new Color(0.35f, 0.2f, 0.2f, 1f);

    public SaveLoadScreen() {
        super();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(0.55f);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(1.1f);
        glyphLayout = new GlyphLayout();

        saveSlotManager = SaveSlotManager.getInstance();
        username = AuthenticationManager.getInstance().getCurrentUsername();
        saveSlotManager.loadSavesForUser(username);
        selectedSlot = 0;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);

        if (actionMessageTimer > 0) actionMessageTimer -= delta;

        handleInput();
        renderUI();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            selectedSlot = (selectedSlot - 1 + 3) % 3;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            selectedSlot = (selectedSlot + 1) % 3;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) selectedSlot = 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) selectedSlot = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) selectedSlot = 2;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            AuthenticationManager.getInstance().logout();
            saveSlotManager.selectSlot(-1);
            ScreenManager.getInstance().setScreen(new AuthenticationScreen());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) handleLoadGame();
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) handleNewGame();

        handleMouseClicks();
    }

    private void handleMouseClicks() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 touch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touch);

            for (int i = 0; i < 3; i++) {
                float slotY = SLOT_START_Y - i * SLOT_GAP;
                if (isInArea(touch.x, touch.y, SLOT_X, slotY, SLOT_W, SLOT_H)) {
                    selectedSlot = i;
                }
            }

            if (isInArea(touch.x, touch.y, BTN1_X, BTN_Y, BTN_W, BTN_H)) handleNewGame();
            if (isInArea(touch.x, touch.y, BTN2_X, BTN_Y, BTN_W, BTN_H)) handleLoadGame();
        }
    }

    private boolean isInArea(float x, float y, float ax, float ay, float aw, float ah) {
        return x >= ax && x <= ax + aw && y >= ay && y <= ay + ah;
    }

    private void renderUI() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < H; i++) {
            float t = i / H;
            float r = COLOR_BG_TOP.r + t * (COLOR_BG_BOTTOM.r - COLOR_BG_TOP.r);
            float g = COLOR_BG_TOP.g + t * (COLOR_BG_BOTTOM.g - COLOR_BG_TOP.g);
            float b = COLOR_BG_TOP.b + t * (COLOR_BG_BOTTOM.b - COLOR_BG_TOP.b);
            shapeRenderer.setColor(r, g, b, 1f);
            shapeRenderer.rect(0, i, W, 1);
        }

        shapeRenderer.setColor(COLOR_HEADER);
        shapeRenderer.rect(0, H - 45f, W, 45f);

        for (int i = 0; i < 3; i++) {
            float slotY = SLOT_START_Y - i * SLOT_GAP;
            SaveSlotManager.SaveSlotData slot = saveSlotManager.getSlot(i + 1);

            shapeRenderer.setColor(selectedSlot == i ? COLOR_SLOT_SELECTED : COLOR_SLOT_UNSELECTED);
            shapeRenderer.rect(SLOT_X, slotY, SLOT_W, SLOT_H);

            shapeRenderer.setColor(slot.hasData() ? COLOR_STATUS_HASDATA : COLOR_STATUS_EMPTY);
            shapeRenderer.rect(SLOT_X + 8f, slotY + SLOT_H - 15f, 8f, 8f);
        }

        shapeRenderer.setColor(COLOR_BTN1);
        shapeRenderer.rect(BTN1_X, BTN_Y, BTN_W, BTN_H);
        shapeRenderer.setColor(COLOR_BTN2);
        shapeRenderer.rect(BTN2_X, BTN_Y, BTN_W, BTN_H);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_BORDER);
        shapeRenderer.line(0, H - 45f, W, H - 45f);

        for (int i = 0; i < 3; i++) {
            float slotY = SLOT_START_Y - i * SLOT_GAP;
            shapeRenderer.setColor(selectedSlot == i ? COLOR_BORDER : new Color(0.6f, 0.15f, 0.15f, 0.6f));
            shapeRenderer.rect(SLOT_X, slotY, SLOT_W, SLOT_H);
        }

        shapeRenderer.setColor(COLOR_BORDER);
        shapeRenderer.rect(BTN1_X, BTN_Y, BTN_W, BTN_H);
        shapeRenderer.rect(BTN2_X, BTN_Y, BTN_W, BTN_H);
        shapeRenderer.end();

        spriteBatch.begin();

        String title = "SELECT SAVE SLOT";
        titleFont.setColor(Color.WHITE);
        glyphLayout.setText(titleFont, title);
        titleFont.draw(spriteBatch, title, CX - glyphLayout.width / 2f, H - 12f);

        String player = "Player: " + username;
        font.setColor(COLOR_TEXT_SUB);
        glyphLayout.setText(font, player);
        font.draw(spriteBatch, player, CX - glyphLayout.width / 2f, H - 33f);

        for (int i = 0; i < 3; i++) {
            float slotY = SLOT_START_Y - i * SLOT_GAP;
            SaveSlotManager.SaveSlotData slot = saveSlotManager.getSlot(i + 1);

            if (slot.hasData()) {
                font.setColor(Color.WHITE);
                font.draw(spriteBatch, slot.toString(), SLOT_X + 22f, slotY + SLOT_H - 10f);
                font.setColor(COLOR_TEXT_DIM);
                font.draw(spriteBatch, "HP: " + slot.getPlayerHP(), SLOT_X + 22f, slotY + 15f);
            } else {
                font.setColor(new Color(0.7f, 0.5f, 0.5f, 1f));
                String empty = "Slot " + (i + 1) + " - Empty";
                glyphLayout.setText(font, empty);
                font.draw(spriteBatch, empty, CX - glyphLayout.width / 2f, slotY + SLOT_H / 2f + 5f);
            }
        }

        font.setColor(Color.WHITE);
        String btn1 = "NEW GAME";
        glyphLayout.setText(font, btn1);
        font.draw(spriteBatch, btn1, BTN1_X + BTN_W / 2f - glyphLayout.width / 2f, BTN_Y + BTN_H / 2f + glyphLayout.height / 2f);

        String btn2 = "LOAD GAME";
        glyphLayout.setText(font, btn2);
        font.draw(spriteBatch, btn2, BTN2_X + BTN_W / 2f - glyphLayout.width / 2f, BTN_Y + BTN_H / 2f + glyphLayout.height / 2f);

        if (actionMessageTimer > 0) {
            font.setColor(new Color(1f, 0.8f, 0.4f, 1f));
            glyphLayout.setText(font, actionMessage);
            font.draw(spriteBatch, actionMessage, CX - glyphLayout.width / 2f, 75f);
        }

        font.setColor(new Color(0.8f, 0.6f, 0.6f, 1f));
        font.getData().setScale(0.45f);
        String instr = "W/S or 1,2,3: select | ENTER: load | N: new | ESC: logout";
        glyphLayout.setText(font, instr);
        font.draw(spriteBatch, instr, CX - glyphLayout.width / 2f, 12f);
        font.getData().setScale(0.55f);

        spriteBatch.end();
    }

    private void handleNewGame() {
        saveSlotManager.selectSlot(selectedSlot + 1);
        if (saveSlotManager.newGame()) {
            setActionMessage("Starting new game in slot " + (selectedSlot + 1) + "...");
            Gdx.app.postRunnable(() -> {
                try { Thread.sleep(800); } catch (InterruptedException e) {}
                ScreenManager.getInstance().setScreen(new PlayScreen());
            });
        }
    }

    private void handleLoadGame() {
        saveSlotManager.selectSlot(selectedSlot + 1);
        SaveSlotManager.SaveSlotData data = saveSlotManager.loadGame();
        if (data != null) {
            setActionMessage("Loading game from slot " + (selectedSlot + 1) + "...");
            Gdx.app.postRunnable(() -> {
                try { Thread.sleep(800); } catch (InterruptedException e) {}
                ScreenManager.getInstance().setScreen(new PlayScreen());
            });
        } else {
            setActionMessage("Slot is empty! Press N for new game.");
        }
    }

    private void setActionMessage(String msg) {
        actionMessage = msg;
        actionMessageTimer = 2f;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
        titleFont.dispose();
    }
}
