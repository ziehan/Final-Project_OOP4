package com.isthereanyone.frontend.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.managers.MyAssetManager;

public class Player {
    public Vector2 position;
<<<<<<< Updated upstream
    private float speed = 100f; // Kecepatan gerak

    // Animasi
    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    private static final int FRAME_WIDTH = 32; // Sesuaikan dengan ukuran sprite (128/4)
    private static final int FRAME_HEIGHT = 32;
=======

    private static final float WALK_SPEED = 80f;
    private static final float RUN_SPEED = 140f;

    public float maxStamina = 100f;
    public float currentStamina = 100f;
    private float staminaDrain = 30f;
    private float staminaRegen = 15f;

    private boolean isExhausted = false;
    private float regenTimer = 0f;
    private final float REGEN_DELAY = 1.0f;

    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean isMoving = false;
    private boolean isRunning = false;
>>>>>>> Stashed changes

    public Player(float startX, float startY) {
        position = new Vector2(startX, startY);

        // Load Texture
        Texture sheet = MyAssetManager.getInstance().get("she.png");
        TextureRegion[][] tmp = TextureRegion.split(sheet, FRAME_WIDTH, FRAME_HEIGHT);

        walkDown  = new Animation<>(0.1f, tmp[0]);
        walkUp    = new Animation<>(0.1f, tmp[1]);
        walkLeft  = new Animation<>(0.1f, tmp[2]);
        walkRight = new Animation<>(0.1f, tmp[3]);

        currentAnimation = walkDown;
        stateTime = 0f;
    }

<<<<<<< Updated upstream
    public void move(float dirX, float dirY, float delta) {
        position.x += dirX * speed * delta;
        position.y += dirY * speed * delta;
        stateTime += delta; // Update timer animasi saat bergerak
=======
    public void move(Vector2 direction, float delta) {
        boolean userHoldsShift = isRunning;
        boolean isDirectionPressed = direction.len2() > 0;

        if (userHoldsShift) {
            regenTimer = 0f;
        } else {
            regenTimer += delta;
        }

        boolean canRun = userHoldsShift && currentStamina > 0 && !isExhausted;
        boolean actuallyRunning = canRun && isDirectionPressed;

        if (actuallyRunning) {
            currentStamina -= staminaDrain * delta;
            if (currentStamina <= 0) {
                currentStamina = 0;
                isExhausted = true;
            }
        } else {
            if (regenTimer >= REGEN_DELAY) {
                currentStamina += staminaRegen * delta;
                if (currentStamina > maxStamina) currentStamina = maxStamina;
                if (isExhausted && currentStamina > 25f) {
                    isExhausted = false;
                }
            }
        }

        float speed = actuallyRunning ? RUN_SPEED : WALK_SPEED;

        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;

        float animSpeed = actuallyRunning ? 1.5f : 1.0f;
        stateTime += delta * animSpeed;

        isMoving = true;
    }

    public void setRunning(boolean running) {
        this.isRunning = running;
    }

    public void setIdle() {
        isMoving = false;
>>>>>>> Stashed changes
    }

    public void setDirection(String direction) {
        switch (direction) {
            case "UP": currentAnimation = walkUp; break;
            case "DOWN": currentAnimation = walkDown; break;
            case "LEFT": currentAnimation = walkLeft; break;
            case "RIGHT": currentAnimation = walkRight; break;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y);
    }
}

