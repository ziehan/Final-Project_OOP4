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

public class RegisterScreen extends BaseScreen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;

    private StringBuilder usernameInput;
    private StringBuilder emailInput;
    private StringBuilder passwordInput;
    private StringBuilder displayNameInput;
    private int selectedField;

    private String statusMessage = "";
    private Color statusColor = Color.WHITE;
    private boolean isLoading = false;

    private static final float FIELD_WIDTH = 200f;
    private static final float FIELD_HEIGHT = 25f;
    private static final float FIELD_SPACING = 45f;
    private static final float CENTER_X = 320f;
    private static final float START_Y = 280f;

    public RegisterScreen() {
        super();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        layout = new GlyphLayout();

        usernameInput = new StringBuilder();
        emailInput = new StringBuilder();
        passwordInput = new StringBuilder();
        displayNameInput = new StringBuilder();
        selectedField = 0;
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
        String[] labels = {"Username", "Email", "Password", "Display Name"};
        for (int i = 0; i < 4; i++) {
            shapeRenderer.setColor(selectedField == i ? Color.DARK_GRAY : Color.GRAY);
            shapeRenderer.rect(CENTER_X - FIELD_WIDTH/2, START_Y - i * FIELD_SPACING, FIELD_WIDTH, FIELD_HEIGHT);
        }
    }

    private void drawButtons() {
        shapeRenderer.setColor(isLoading ? Color.GRAY : Color.FOREST);
        shapeRenderer.rect(CENTER_X - 110, START_Y - 200, 100, 35);

        shapeRenderer.setColor(Color.NAVY);
        shapeRenderer.rect(CENTER_X + 10, START_Y - 200, 100, 35);
    }

    private void drawLabels() {
        font.setColor(Color.WHITE);

        layout.setText(font, "REGISTER");
        font.draw(batch, "REGISTER", CENTER_X - layout.width/2, START_Y + 60);

        String[] labels = {"Username:", "Email:", "Password:", "Display Name (optional):"};
        StringBuilder[] inputs = {usernameInput, emailInput, passwordInput, displayNameInput};

        for (int i = 0; i < 4; i++) {
            font.setColor(Color.WHITE);
            font.draw(batch, labels[i], CENTER_X - FIELD_WIDTH/2, START_Y - i * FIELD_SPACING + 38);

            String text = i == 2 ? "*".repeat(inputs[i].length()) : inputs[i].toString();
            font.draw(batch, text, CENTER_X - FIELD_WIDTH/2 + 5, START_Y - i * FIELD_SPACING + 18);

            if (selectedField == i) {
                font.setColor(Color.LIGHT_GRAY);
                float cursorX = CENTER_X - FIELD_WIDTH/2 + 5 + getTextWidth(text);
                font.draw(batch, "_", cursorX, START_Y - i * FIELD_SPACING + 18);
            }
        }

        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "[TAB] Switch field  [ENTER] Register", CENTER_X - 110, START_Y - 240);
    }

    private void drawButtonLabels() {
        font.setColor(Color.WHITE);

        layout.setText(font, "Register");
        font.draw(batch, "Register", CENTER_X - 60 - layout.width/2, START_Y - 185);

        layout.setText(font, "Back");
        font.draw(batch, "Back", CENTER_X + 60 - layout.width/2, START_Y - 185);
    }

    private void drawStatusMessage() {
        if (!statusMessage.isEmpty()) {
            font.setColor(statusColor);
            layout.setText(font, statusMessage);
            font.draw(batch, statusMessage, CENTER_X - layout.width/2, START_Y + 85);
        }
    }

    private float getTextWidth(String text) {
        layout.setText(font, text);
        return layout.width;
    }

    private void handleInput() {
        if (isLoading) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            selectedField = (selectedField + 1) % 4;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            attemptRegister();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ScreenManager.getInstance().setScreen(new LoginScreen());
        }

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX() * (camera.viewportWidth / Gdx.graphics.getWidth());
            float touchY = camera.viewportHeight - Gdx.input.getY() * (camera.viewportHeight / Gdx.graphics.getHeight());

            for (int i = 0; i < 4; i++) {
                if (isInBounds(touchX, touchY, CENTER_X - FIELD_WIDTH/2, START_Y - i * FIELD_SPACING, FIELD_WIDTH, FIELD_HEIGHT)) {
                    selectedField = i;
                    break;
                }
            }

            if (isInBounds(touchX, touchY, CENTER_X - 110, START_Y - 200, 100, 35)) {
                attemptRegister();
            }

            if (isInBounds(touchX, touchY, CENTER_X + 10, START_Y - 200, 100, 35)) {
                ScreenManager.getInstance().setScreen(new LoginScreen());
            }
        }

        handleTextInput();
    }

    private void handleTextInput() {
        StringBuilder[] inputs = {usernameInput, emailInput, passwordInput, displayNameInput};
        StringBuilder currentField = inputs[selectedField];

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
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            currentField.append('_');
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            currentField.append('-');
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentField.append('@');
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) currentField.append(' ');
    }

    private boolean isInBounds(float x, float y, float bx, float by, float bw, float bh) {
        return x >= bx && x <= bx + bw && y >= by && y <= by + bh;
    }

    private void attemptRegister() {
        String username = usernameInput.toString().trim();
        String email = emailInput.toString().trim();
        String password = passwordInput.toString();
        String displayName = displayNameInput.toString().trim();

        if (username.isEmpty()) {
            statusMessage = "Username is required!";
            statusColor = Color.RED;
            return;
        }
        if (username.length() < 3) {
            statusMessage = "Username must be at least 3 characters!";
            statusColor = Color.RED;
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            statusMessage = "Valid email is required!";
            statusColor = Color.RED;
            return;
        }
        if (password.length() < 6) {
            statusMessage = "Password must be at least 6 characters!";
            statusColor = Color.RED;
            return;
        }

        isLoading = true;
        statusMessage = "Registering...";
        statusColor = Color.YELLOW;

        NetworkManager.getInstance().signup(username, email, password,
            displayName.isEmpty() ? username : displayName,
            new NetworkCallback<ApiResponse<AuthResponse>>() {
                @Override
                public void onSuccess(ApiResponse<AuthResponse> result) {
                    isLoading = false;
                    if (result.isSuccess()) {
                        statusMessage = "Registration successful!";
                        statusColor = Color.GREEN;
                        // Navigate to main menu
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

