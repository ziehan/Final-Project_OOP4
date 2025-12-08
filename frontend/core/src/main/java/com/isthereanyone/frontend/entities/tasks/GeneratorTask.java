package com.isthereanyone.frontend.entities.tasks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.entities.Player;

public class GeneratorTask extends BaseTask {
    private static final float BAR_WIDTH = 250f;
    private static final float BAR_HEIGHT = 20f;
    private static final float CURSOR_WIDTH = 4f;

    private static final int REQUIRED_SUCCESS = 3;
    private static final int MAX_LIVES = 3;
    private static final float MIN_TARGET_PIXELS = 20f;

    private int currentSuccess = 0;
    private int currentLives = MAX_LIVES;

    private float cursorPosition = 0.5f;
    private float baseSpeed = 1.0f;
    private int cursorDirection = 1;

    private float targetCenter = 0.5f;
    private float currentTargetWidthPixels = 80f;

    private float erraticTimer = 0f;
    private float speedMultiplier = 1.0f;

    private float flickerTimer = 0f;
    private boolean isLightOn = true;

    private float feedbackTimer = 0f;
    private Color feedbackColor = Color.CLEAR;
    private boolean isWaiting = false;
    private boolean lastRoundSuccess = false;

    public GeneratorTask(float x, float y) {
        super(x, y);
        resetFull();
    }

    @Override
    protected void executeLogic() {
        System.out.println("Generator Start.");
    }

    @Override
    public boolean updateMinigame(float delta, Viewport viewport, Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) return true;

        if (isWaiting) {
            feedbackTimer -= delta;
            if (feedbackTimer <= 0) {
                isWaiting = false;
                feedbackColor = Color.CLEAR;
                finalizeRound();
            }
            return isCompleted;
        }

        updateErraticSpeed(delta);
        updateFlicker(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            checkHit();
        }

        float finalSpeed = baseSpeed * speedMultiplier;

        cursorPosition += finalSpeed * cursorDirection * delta;

        if (cursorPosition >= 1.0f) {
            cursorPosition = 1.0f;
            cursorDirection = -1;
        } else if (cursorPosition <= 0.0f) {
            cursorPosition = 0.0f;
            cursorDirection = 1;
        }

        return isCompleted;
    }

    private void updateErraticSpeed(float delta) {
        erraticTimer -= delta;
        if (erraticTimer <= 0) {
            speedMultiplier = MathUtils.random(0.5f, 2.5f);
            erraticTimer = MathUtils.random(0.2f, 0.5f);
        }
    }

    private void updateFlicker(float delta) {
        flickerTimer -= delta;
        if (flickerTimer <= 0) {
            isLightOn = !isLightOn;

            if (isLightOn) {
                flickerTimer = MathUtils.random(0.5f, 1.5f);
            } else {
                flickerTimer = MathUtils.random(0.1f, 0.3f);
            }
        }
    }

    private void checkHit() {
        float cursorPixelCenter = cursorPosition * BAR_WIDTH;
        float targetPixelCenter = targetCenter * BAR_WIDTH;
        float dist = Math.abs(cursorPixelCenter - targetPixelCenter);
        float tolerance = (currentTargetWidthPixels / 2f) + (CURSOR_WIDTH / 2f);

        if (dist <= tolerance) {
            processResult(true);
        } else {
            processResult(false);
        }
    }

    private void processResult(boolean success) {
        lastRoundSuccess = success;
        isWaiting = true;
        feedbackTimer = 0.5f;

        isLightOn = true;
        speedMultiplier = 1.0f;

        if (success) {
            feedbackColor = new Color(0f, 1f, 0f, 0.4f);
            currentSuccess++;
        } else {
            feedbackColor = new Color(1f, 0f, 0f, 0.4f);
            currentLives--;
        }
    }

    private void finalizeRound() {
        if (lastRoundSuccess) {
            if (currentSuccess >= REQUIRED_SUCCESS) {
                completeTask();
            } else {
                currentTargetWidthPixels *= 0.65f;
                if (currentTargetWidthPixels < MIN_TARGET_PIXELS) currentTargetWidthPixels = MIN_TARGET_PIXELS;

                // Base speed naik dikit
                baseSpeed += 0.3f;
                if (baseSpeed > 2.0f) baseSpeed = 2.0f;

                cursorPosition = 0.5f;
                randomizeTarget();
            }
        } else {
            if (currentLives <= 0) {
                resetFull();
            } else {
                cursorPosition = 0.5f;
                randomizeTarget();
            }
        }
    }

    private void resetFull() {
        currentSuccess = 0;
        currentLives = MAX_LIVES;
        baseSpeed = 1.0f;
        speedMultiplier = 1.0f;
        isLightOn = true;
        cursorPosition = 0.5f;
        currentTargetWidthPixels = 80f;
        randomizeTarget();
    }

    private void randomizeTarget() {
        float marginPercent = (currentTargetWidthPixels / 2f) / BAR_WIDTH;
        if (marginPercent > 0.45f) marginPercent = 0.45f;
        targetCenter = MathUtils.random(marginPercent, 1.0f - marginPercent);
    }

    @Override
    public void renderMinigame(SpriteBatch batch, ShapeRenderer shape, Viewport viewport) {
        shape.begin(ShapeRenderer.ShapeType.Filled);

        float cx = viewport.getCamera().position.x;
        float cy = viewport.getCamera().position.y;
        float vw = viewport.getWorldWidth();
        float vh = viewport.getWorldHeight();

        float margin = 10f;
        float squareSize = Math.min(vw, vh) - (margin * 2);

        float panelX = cx - (squareSize / 2);
        float panelY = cy - (squareSize / 2);

        shape.setColor(0.1f, 0.1f, 0.1f, 0.95f);
        shape.rect(panelX, panelY, squareSize, squareSize);

        if (feedbackTimer > 0) {
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            shape.setColor(feedbackColor);
            shape.rect(panelX, panelY, squareSize, squareSize);
        }

        float barLeftX = cx - (BAR_WIDTH / 2);
        float barY = cy;

        shape.setColor(0.05f, 0.05f, 0.05f, 1f);
        shape.rect(barLeftX, barY - (BAR_HEIGHT/2), BAR_WIDTH, BAR_HEIGHT);

        if (isLightOn) {
            float targetPixelX = barLeftX + (targetCenter * BAR_WIDTH);
            shape.setColor(0f, 1f, 0f, 0.8f);
            shape.rect(targetPixelX - (currentTargetWidthPixels / 2), barY - (BAR_HEIGHT/2), currentTargetWidthPixels, BAR_HEIGHT);

            float cursorPixelX = barLeftX + (cursorPosition * BAR_WIDTH);
            shape.setColor(Color.WHITE);
            float cursorH = BAR_HEIGHT + 10;
            shape.rect(cursorPixelX - (CURSOR_WIDTH / 2), barY - (cursorH/2), CURSOR_WIDTH, cursorH);
        } else {
            shape.setColor(0f, 0f, 0f, 0.5f);
            shape.rect(barLeftX, barY - (BAR_HEIGHT/2), BAR_WIDTH, BAR_HEIGHT);
        }

        drawIndicators(shape, cx, barY + 50);

        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.GRAY);
        shape.rect(panelX, panelY, squareSize, squareSize);
        shape.end();
    }

    private void drawIndicators(ShapeRenderer shape, float centerX, float centerY) {
        float lampSize = 12;
        float gap = 5;
        float totalW = (REQUIRED_SUCCESS * lampSize) + ((REQUIRED_SUCCESS-1) * gap);
        float startX = centerX - (totalW / 2);

        for (int i = 0; i < REQUIRED_SUCCESS; i++) {
            if (i < currentSuccess) shape.setColor(Color.GREEN);
            else shape.setColor(0.3f, 0.3f, 0.3f, 1f);

            shape.rect(startX + (i * (lampSize + gap)), centerY, lampSize, lampSize);
        }

        float lifeY = centerY - 100;
        float lifeSize = 10;
        float lifeGap = 8;
        float lifeTotalW = (MAX_LIVES * lifeSize) + ((MAX_LIVES-1) * gap);
        float lifeStartX = centerX - (lifeTotalW / 2);

        shape.setColor(0.8f, 0f, 0f, 1f);
        for (int i = 0; i < currentLives; i++) {
            float x = lifeStartX + (i * (lifeSize + gap));
            shape.rect(x, lifeY, lifeSize, lifeSize);
        }
    }
}
