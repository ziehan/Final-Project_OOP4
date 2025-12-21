package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.isthereanyone.frontend.managers.NetworkManager;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.network.NetworkCallback;
import com.isthereanyone.frontend.network.dto.ApiResponse;
import com.isthereanyone.frontend.network.dto.AuthResponse;

public class LoginScreen extends BaseScreen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;

    private StringBuilder usernameInput;
    private StringBuilder passwordInput;
    private int selectedField;

    private String statusMessage = "";
    private Color statusColor = Color.WHITE;
    private boolean isLoading = false;

    private static final float FIELD_WIDTH = 200f;
    private static final float FIELD_HEIGHT = 30f;
    private static final float FIELD_SPACING = 50f;
    private static final float CENTER_X = 320f;
    private static final float START_Y = 220f;

    public LoginScreen() {
        super();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        layout = new GlyphLayout();

        usernameInput = new StringBuilder();
        passwordInput = new StringBuilder();
        selectedField = 0;

        checkServerConnection();
    }

    private void checkServerConnection() {
        statusMessage = "Checking server connection...";
        statusColor = Color.YELLOW;

        NetworkManager.getInstance().ping(new NetworkCallback<String>() {
            @Override
            public void onSuccess(String result) {
                statusMessage = "Server connected. Enter your credentials.";
                statusColor = Color.GREEN;
            }

            @Override
            public void onFailure(String error) {
                statusMessage = "Cannot connect to server!";
                statusColor = Color.RED;
            }
        });
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.1f, 0.05f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawInputFields();
        drawButtons();
        shapeRenderer.end();

        batch.begin();
        drawLabels();
        drawButtonLabels();
        drawStatusMessage();
        batch.end();
    }

    private void drawInputFields() {
        shapeRenderer.setColor(selectedField == 0 ? Color.DARK_GRAY : Color.GRAY);
        shapeRenderer.rect(CENTER_X - FIELD_WIDTH/2, START_Y, FIELD_WIDTH, FIELD_HEIGHT);

        shapeRenderer.setColor(selectedField == 1 ? Color.DARK_GRAY : Color.GRAY);
        shapeRenderer.rect(CENTER_X - FIELD_WIDTH/2, START_Y - FIELD_SPACING, FIELD_WIDTH, FIELD_HEIGHT);
    }

    private void drawButtons() {
        shapeRenderer.setColor(isLoading ? Color.GRAY : Color.FOREST);
        shapeRenderer.rect(CENTER_X - 110, START_Y - 120, 100, 35);

        shapeRenderer.setColor(Color.NAVY);
        shapeRenderer.rect(CENTER_X + 10, START_Y - 120, 100, 35);

        shapeRenderer.setColor(Color.MAROON);
        shapeRenderer.rect(CENTER_X - 50, START_Y - 170, 100, 30);
    }

    private void drawLabels() {
        font.setColor(Color.WHITE);

        layout.setText(font, "LOGIN");
        font.draw(batch, "LOGIN", CENTER_X - layout.width/2, START_Y + 80);

        font.draw(batch, "Username/Email:", CENTER_X - FIELD_WIDTH/2, START_Y + 45);
        font.draw(batch, "Password:", CENTER_X - FIELD_WIDTH/2, START_Y - FIELD_SPACING + 45);

        font.setColor(Color.WHITE);
        font.draw(batch, usernameInput.toString(), CENTER_X - FIELD_WIDTH/2 + 5, START_Y + 22);

        String maskedPassword = "*".repeat(passwordInput.length());
        font.draw(batch, maskedPassword, CENTER_X - FIELD_WIDTH/2 + 5, START_Y - FIELD_SPACING + 22);

        font.setColor(Color.LIGHT_GRAY);
        if (selectedField == 0) {
            float cursorX = CENTER_X - FIELD_WIDTH/2 + 5 + getTextWidth(usernameInput.toString());
            font.draw(batch, "_", cursorX, START_Y + 22);
        } else {
            float cursorX = CENTER_X - FIELD_WIDTH/2 + 5 + getTextWidth(maskedPassword);
            font.draw(batch, "_", cursorX, START_Y - FIELD_SPACING + 22);
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[TAB] Switch field  [ENTER] Login", CENTER_X - 100, START_Y - 200);
    }

    private void drawButtonLabels() {
        font.setColor(Color.WHITE);

        layout.setText(font, "Login");
        font.draw(batch, "Login", CENTER_X - 60 - layout.width/2, START_Y - 105);

        layout.setText(font, "Register");
        font.draw(batch, "Register", CENTER_X + 60 - layout.width/2, START_Y - 105);

        layout.setText(font, "Back");
        font.draw(batch, "Back", CENTER_X - layout.width/2, START_Y - 160);
    }

    private void drawStatusMessage() {
        if (!statusMessage.isEmpty()) {
            font.setColor(statusColor);
            layout.setText(font, statusMessage);
            font.draw(batch, statusMessage, CENTER_X - layout.width/2, START_Y + 110);
        }
    }

    private float getTextWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }

    private void handleInput() {
        if (isLoading) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            selectedField = (selectedField + 1) % 2;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            attemptLogin();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ScreenManager.getInstance().setScreen(new MainMenuScreen());
        }

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX() * (camera.viewportWidth / Gdx.graphics.getWidth());
            float touchY = camera.viewportHeight - Gdx.input.getY() * (camera.viewportHeight / Gdx.graphics.getHeight());

            if (isInBounds(touchX, touchY, CENTER_X - FIELD_WIDTH/2, START_Y, FIELD_WIDTH, FIELD_HEIGHT)) {
                selectedField = 0;
            } else if (isInBounds(touchX, touchY, CENTER_X - FIELD_WIDTH/2, START_Y - FIELD_SPACING, FIELD_WIDTH, FIELD_HEIGHT)) {
                selectedField = 1;
            }

            if (isInBounds(touchX, touchY, CENTER_X - 110, START_Y - 120, 100, 35)) {
                attemptLogin();
            }

            if (isInBounds(touchX, touchY, CENTER_X + 10, START_Y - 120, 100, 35)) {
                ScreenManager.getInstance().setScreen(new RegisterScreen());
            }

            if (isInBounds(touchX, touchY, CENTER_X - 50, START_Y - 170, 100, 30)) {
                ScreenManager.getInstance().setScreen(new MainMenuScreen());
            }
        }

        handleTextInput();
    }

    private void handleTextInput() {
        StringBuilder currentField = selectedField == 0 ? usernameInput : passwordInput;

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE) && currentField.length() > 0) {
            currentField.deleteCharAt(currentField.length() - 1);
        }

        for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                char c = (char) ('a' + (i - Input.Keys.A));
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                    c = Character.toUpperCase(c);
                }
                currentField.append(c);
            }
        }

        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                currentField.append((char) ('0' + (i - Input.Keys.NUM_0)));
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD)) currentField.append('.');
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) currentField.append('-');
        if (Gdx.input.isKeyJustPressed(Input.Keys.AT)) currentField.append('@');
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentField.append('@');
        }
    }

    private boolean isInBounds(float x, float y, float bx, float by, float bw, float bh) {
        return x >= bx && x <= bx + bw && y >= by && y <= by + bh;
    }

    private void attemptLogin() {
        String username = usernameInput.toString().trim();
        String password = passwordInput.toString();

        if (username.isEmpty() || password.isEmpty()) {
            statusMessage = "Please fill all fields!";
            statusColor = Color.RED;
            return;
        }

        isLoading = true;
        statusMessage = "Logging in...";
        statusColor = Color.YELLOW;

        NetworkManager.getInstance().login(username, password, new NetworkCallback<ApiResponse<AuthResponse>>() {
            @Override
            public void onSuccess(ApiResponse<AuthResponse> result) {
                isLoading = false;
                if (result.isSuccess()) {
                    statusMessage = "Login successful!";
                    statusColor = Color.GREEN;
                    Gdx.app.postRunnable(() -> {
                        ScreenManager.getInstance().setScreen(new MainMenuScreen());
                    });
                } else {
                    statusMessage = result.getMessage();
                    statusColor = Color.RED;
                }
            }

            @Override
            public void onFailure(String error) {
                isLoading = false;
                statusMessage = "Connection error: " + error;
                statusColor = Color.RED;
            }
        });
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}

