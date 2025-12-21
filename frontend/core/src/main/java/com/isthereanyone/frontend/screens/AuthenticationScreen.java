package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.managers.AuthenticationManager;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.network.NetworkCallback;
import com.isthereanyone.frontend.network.dto.ApiResponse;
import com.isthereanyone.frontend.network.dto.AuthResponse;

public class AuthenticationScreen extends BaseScreen {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private BitmapFont titleFont;
    private GlyphLayout glyphLayout;

    private AuthState currentState = AuthState.LOGIN;
    private StringBuilder usernameInput = new StringBuilder();
    private StringBuilder passwordInput = new StringBuilder();
    private String errorMessage = "";
    private float errorMessageTimer = 0f;
    private int focusedField = 0;
    private float cursorBlink = 0f;
    private InputProcessor inputProcessor;
    private boolean isLoading = false;

    private static final float W = GameConfig.VIEWPORT_WIDTH;
    private static final float H = GameConfig.VIEWPORT_HEIGHT;
    private static final float CX = W / 2f;
    private static final float CY = H / 2f;

    private static final float PANEL_W = 260f;
    private static final float PANEL_H = 200f;
    private static final float PANEL_X = CX - PANEL_W / 2f;
    private static final float PANEL_Y = CY - PANEL_H / 2f;

    private static final float FIELD_W = 200f;
    private static final float FIELD_H = 22f;
    private static final float FIELD_X = CX - FIELD_W / 2f;
    private static final float USER_FIELD_Y = PANEL_Y + PANEL_H - 80f;
    private static final float PASS_FIELD_Y = PANEL_Y + PANEL_H - 130f;

    private static final float BTN_W = 90f;
    private static final float BTN_H = 28f;
    private static final float BTN_GAP = 10f;
    private static final float BTN_Y = PANEL_Y + 25f;
    private static final float BTN1_X = CX - BTN_W - BTN_GAP / 2f;
    private static final float BTN2_X = CX + BTN_GAP / 2f;

    private static final Color COLOR_BG = new Color(0.08f, 0.05f, 0.05f, 0.98f);
    private static final Color COLOR_FIELD_BG = new Color(0.15f, 0.08f, 0.08f, 1f);
    private static final Color COLOR_BTN1 = new Color(0.5f, 0.15f, 0.15f, 1f);
    private static final Color COLOR_BTN2 = new Color(0.4f, 0.12f, 0.12f, 1f);
    private static final Color COLOR_BORDER = new Color(0.7f, 0.15f, 0.15f, 1f);
    private static final Color COLOR_BORDER_FOCUS = new Color(1f, 0.3f, 0.3f, 1f);
    private static final Color COLOR_TEXT_DIM = new Color(0.8f, 0.6f, 0.6f, 1f);

    private enum AuthState { LOGIN, SIGNUP }

    public AuthenticationScreen() {
        super();
        shapeRenderer = new ShapeRenderer();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(0.55f);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(1.1f);
        glyphLayout = new GlyphLayout();

        inputProcessor = new InputAdapter() {
            @Override
            public boolean keyTyped(char character) {
                if (Character.isLetterOrDigit(character) || character == '_' || character == '.' || character == '@' || character == '-') {
                    if (focusedField == 0 && usernameInput.length() < 18) {
                        usernameInput.append(character);
                    } else if (focusedField == 1 && passwordInput.length() < 18) {
                        passwordInput.append(character);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACKSPACE) {
                    if (focusedField == 0 && usernameInput.length() > 0) {
                        usernameInput.deleteCharAt(usernameInput.length() - 1);
                    } else if (focusedField == 1 && passwordInput.length() > 0) {
                        passwordInput.deleteCharAt(passwordInput.length() - 1);
                    }
                    return true;
                }
                if (keycode == Input.Keys.TAB) {
                    focusedField = 1 - focusedField;
                    return true;
                }
                if (keycode == Input.Keys.ESCAPE) {
                    resetInputs();
                    ScreenManager.getInstance().setScreen(new MainMenuScreen());
                    return true;
                }
                if (keycode == Input.Keys.ENTER) {
                    handleAuthAction();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    Vector3 touch = new Vector3(screenX, screenY, 0);
                    viewport.unproject(touch);

                    if (isInArea(touch.x, touch.y, FIELD_X, USER_FIELD_Y, FIELD_W, FIELD_H)) {
                        focusedField = 0;
                        return true;
                    }
                    if (isInArea(touch.x, touch.y, FIELD_X, PASS_FIELD_Y, FIELD_W, FIELD_H)) {
                        focusedField = 1;
                        return true;
                    }
                    if (isInArea(touch.x, touch.y, BTN1_X, BTN_Y, BTN_W, BTN_H)) {
                        handleAuthAction();
                        return true;
                    }
                    if (isInArea(touch.x, touch.y, BTN2_X, BTN_Y, BTN_W, BTN_H)) {
                        toggleAuthState();
                        return true;
                    }
                }
                return false;
            }
        };
        Gdx.input.setInputProcessor(inputProcessor);
    }

    private boolean isInArea(float x, float y, float ax, float ay, float aw, float ah) {
        return x >= ax && x <= ax + aw && y >= ay && y <= ay + ah;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.03f, 0.03f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        spriteBatch.setProjectionMatrix(camera.combined);

        if (errorMessageTimer > 0) errorMessageTimer -= delta;
        cursorBlink += delta;

        renderUI();
    }

    private void renderUI() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < H; i++) {
            float t = i / H;
            shapeRenderer.setColor(0.04f + t * 0.03f, 0.02f + t * 0.02f, 0.02f + t * 0.03f, 1f);
            shapeRenderer.rect(0, i, W, 1);
        }

        shapeRenderer.setColor(0.15f, 0f, 0f, 0.4f);
        shapeRenderer.rect(PANEL_X + 3, PANEL_Y - 3, PANEL_W, PANEL_H);
        shapeRenderer.setColor(COLOR_BG);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);

        shapeRenderer.setColor(COLOR_FIELD_BG);
        shapeRenderer.rect(FIELD_X, USER_FIELD_Y, FIELD_W, FIELD_H);
        shapeRenderer.rect(FIELD_X, PASS_FIELD_Y, FIELD_W, FIELD_H);

        shapeRenderer.setColor(COLOR_BTN1);
        shapeRenderer.rect(BTN1_X, BTN_Y, BTN_W, BTN_H);
        shapeRenderer.setColor(COLOR_BTN2);
        shapeRenderer.rect(BTN2_X, BTN_Y, BTN_W, BTN_H);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(COLOR_BORDER);
        shapeRenderer.rect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H);
        shapeRenderer.setColor(focusedField == 0 ? COLOR_BORDER_FOCUS : COLOR_BORDER);
        shapeRenderer.rect(FIELD_X, USER_FIELD_Y, FIELD_W, FIELD_H);
        shapeRenderer.setColor(focusedField == 1 ? COLOR_BORDER_FOCUS : COLOR_BORDER);
        shapeRenderer.rect(FIELD_X, PASS_FIELD_Y, FIELD_W, FIELD_H);
        shapeRenderer.end();

        spriteBatch.begin();

        String title = currentState == AuthState.LOGIN ? "LOGIN" : "SIGN UP";
        titleFont.setColor(1f, 0.3f, 0.3f, 1f);
        glyphLayout.setText(titleFont, title);
        titleFont.draw(spriteBatch, title, CX - glyphLayout.width / 2f, PANEL_Y + PANEL_H - 20f);

        font.setColor(COLOR_TEXT_DIM);
        font.draw(spriteBatch, "Username:", FIELD_X, USER_FIELD_Y + FIELD_H + 14f);
        font.draw(spriteBatch, "Password:", FIELD_X, PASS_FIELD_Y + FIELD_H + 14f);

        font.setColor(Color.WHITE);
        font.draw(spriteBatch, usernameInput.toString(), FIELD_X + 6f, USER_FIELD_Y + FIELD_H / 2f + 5f);

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < passwordInput.length(); i++) stars.append("*");
        font.draw(spriteBatch, stars.toString(), FIELD_X + 6f, PASS_FIELD_Y + FIELD_H / 2f + 5f);

        if (cursorBlink % 1f < 0.5f) {
            if (focusedField == 0) {
                glyphLayout.setText(font, usernameInput.toString());
                font.draw(spriteBatch, "|", FIELD_X + 6f + glyphLayout.width, USER_FIELD_Y + FIELD_H / 2f + 5f);
            } else {
                glyphLayout.setText(font, stars.toString());
                font.draw(spriteBatch, "|", FIELD_X + 6f + glyphLayout.width, PASS_FIELD_Y + FIELD_H / 2f + 5f);
            }
        }

        font.setColor(Color.WHITE);
        String btn1 = currentState == AuthState.LOGIN ? "LOGIN" : "SIGN UP";
        glyphLayout.setText(font, btn1);
        font.draw(spriteBatch, btn1, BTN1_X + BTN_W / 2f - glyphLayout.width / 2f, BTN_Y + BTN_H / 2f + glyphLayout.height / 2f);

        String btn2 = currentState == AuthState.LOGIN ? "SIGN UP" : "BACK";
        glyphLayout.setText(font, btn2);
        font.draw(spriteBatch, btn2, BTN2_X + BTN_W / 2f - glyphLayout.width / 2f, BTN_Y + BTN_H / 2f + glyphLayout.height / 2f);

        if (errorMessageTimer > 0) {
            font.setColor(1f, 0.4f, 0.4f, 1f);
            glyphLayout.setText(font, errorMessage);
            font.draw(spriteBatch, errorMessage, CX - glyphLayout.width / 2f, PANEL_Y - 12f);
        }

        font.setColor(0.6f, 0.4f, 0.4f, 1f);
        font.getData().setScale(0.45f);
        String instr = "TAB: switch | ENTER: submit | ESC: back";
        glyphLayout.setText(font, instr);
        font.draw(spriteBatch, instr, CX - glyphLayout.width / 2f, 18f);
        font.getData().setScale(0.55f);

        spriteBatch.end();
    }

    private void handleAuthAction() {
        if (isLoading) return;

        String username = usernameInput.toString().trim();
        String password = passwordInput.toString();

        if (username.isEmpty() || password.isEmpty()) {
            setErrorMessage("Username dan password tidak boleh kosong!");
            return;
        }

        isLoading = true;
        setErrorMessage("Loading...");

        AuthenticationManager auth = AuthenticationManager.getInstance();
        if (currentState == AuthState.LOGIN) {
            auth.login(username, password, new NetworkCallback<ApiResponse<AuthResponse>>() {
                @Override
                public void onSuccess(ApiResponse<AuthResponse> result) {
                    isLoading = false;
                    if (result.isSuccess()) {
                        resetInputs();
                        ScreenManager.getInstance().setScreen(new SaveSlotScreen());
                    } else {
                        setErrorMessage(result.getMessage() != null ? result.getMessage() : "Login gagal!");
                    }
                }

                @Override
                public void onFailure(String error) {
                    isLoading = false;
                    setErrorMessage("Koneksi error: " + error);
                }
            });
        } else {
            String email = username + "@game.local";
            auth.signup(username, email, password, username, new NetworkCallback<ApiResponse<AuthResponse>>() {
                @Override
                public void onSuccess(ApiResponse<AuthResponse> result) {
                    isLoading = false;
                    if (result.isSuccess()) {
                        resetInputs();
                        setErrorMessage("Signup berhasil! Silakan login.");
                        currentState = AuthState.LOGIN;
                    } else {
                        setErrorMessage(result.getMessage() != null ? result.getMessage() : "Signup gagal!");
                    }
                }

                @Override
                public void onFailure(String error) {
                    isLoading = false;
                    setErrorMessage("Koneksi error: " + error);
                }
            });
        }
    }

    private void toggleAuthState() {
        resetInputs();
        currentState = currentState == AuthState.LOGIN ? AuthState.SIGNUP : AuthState.LOGIN;
        errorMessage = "";
    }

    private void setErrorMessage(String msg) {
        errorMessage = msg;
        errorMessageTimer = 3f;
    }

    private void resetInputs() {
        usernameInput.setLength(0);
        passwordInput.setLength(0);
        errorMessage = "";
        errorMessageTimer = 0;
        focusedField = 0;
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(inputProcessor); }

    @Override
    public void hide() { Gdx.input.setInputProcessor(null); }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        font.dispose();
        titleFont.dispose();
    }
}
