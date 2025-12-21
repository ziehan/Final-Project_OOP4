package com.isthereanyone.frontend.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.components.Inventory;
import com.isthereanyone.frontend.managers.MyAssetManager;

// biar bisa commit lagi

public class Player {
    public enum State { IDLE, WALK, RUN, HURT, DEAD }
    public State currentState = State.IDLE;

    public Vector2 position;
    private static final float WALK_SPEED = 80f;
    private static final float RUN_SPEED = 140f;

    public int health = 2;
    public boolean isDead = false;
    public boolean isHidden = false;
    private boolean isInvincible = false;
    private float hurtTimer = 0f;


    public float maxStamina = 100f;
    public float currentStamina = 100f;
    private float staminaDrain = 30f;
    private float staminaRegen = 15f;
    private boolean isExhausted = false;
    private float regenTimer = 0f;
    private final float REGEN_DELAY = 1.0f; // 1 second delay before stamina starts recharging

    // Movement tracking for SFX
    private boolean isCurrentlyMoving = false;
    private boolean wasMovingLastFrame = false;

    private Animation<TextureRegion>[] idleAnims;
    private Animation<TextureRegion>[] walkAnims;
    private Animation<TextureRegion>[] runAnims;
    private Animation<TextureRegion>[] hurtAnims;
    private Animation<TextureRegion>[] deathAnims;

    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;
    private int facingDirection = 0;

    public Inventory inventory;

    public Player(float startX, float startY) {
        position = new Vector2(startX, startY);
        inventory = new Inventory();

        float[] idleDurations = {0.25f, 0.25f, 0.25f, 1f};
        idleAnims = loadCustomAnimation("Unarmed_Idle_with_shadow.png", new int[]{12, 12, 12, 4}, idleDurations, true);

        walkAnims = loadCustomAnimation("Unarmed_Walk_with_shadow.png", new int[]{6, 6, 6, 6}, 0.15f, true);
        runAnims  = loadCustomAnimation("Unarmed_Run_with_shadow.png", new int[]{8, 8, 8, 8}, 0.1f, true);
        hurtAnims = loadCustomAnimation("Unarmed_Hurt_with_shadow.png", new int[]{5, 5, 5, 5}, 0.2f, false);
        deathAnims= loadCustomAnimation("Unarmed_Death_with_shadow.png", new int[]{7, 7, 7, 7}, 0.2f, false);

        currentState = State.IDLE;
        currentAnimation = idleAnims[0];
    }

    private Animation<TextureRegion>[] loadCustomAnimation(String fileName, int[] framesPerDir, float frameDuration, boolean loop) {
        float[] durations = {frameDuration, frameDuration, frameDuration, frameDuration};
        return loadCustomAnimation(fileName, framesPerDir, durations, loop);
    }

    private Animation<TextureRegion>[] loadCustomAnimation(String fileName, int[] framesPerDir, float[] frameDurations, boolean loop) {
        Texture sheet = MyAssetManager.getInstance().get(fileName);

        int maxFramesInRow = 0;
        for (int count : framesPerDir) {
            if (count > maxFramesInRow) maxFramesInRow = count;
        }

        int frameWidth = sheet.getWidth() / maxFramesInRow;
        int frameHeight = sheet.getHeight() / 4;

        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);

        @SuppressWarnings("unchecked")
        Animation<TextureRegion>[] anims = new Animation[4];

        for (int i = 0; i < 4; i++) {
            TextureRegion[] validFrames = new TextureRegion[framesPerDir[i]];
            for (int j = 0; j < framesPerDir[i]; j++) {
                validFrames[j] = tmp[i][j];
            }
            anims[i] = new Animation<>(frameDurations[i], validFrames);
            anims[i].setPlayMode(loop ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        }
        return anims;
    }

    public void takeDamage() {
        if (isInvincible || isDead) return;

        if (isHidden) {
            isHidden = false;
            position.y -= 32f;

            System.out.println("GHOST: Found you! Forced out of hiding!");

            return;
        }

        health--;
        System.out.println("Player hit. HP left: " + health);

        if (health <= 0) {
            die();
        } else {
            currentState = State.HURT;
            stateTime = 0;
            hurtTimer = 0.5f;
            isInvincible = true;
        }
    }

    private void die() {
        isDead = true;
        currentState = State.DEAD;
        stateTime = 0;
        System.out.println("Player is dead");
    }

    public void update(float delta) {
        stateTime += delta;

        if (currentState == State.DEAD) {
            currentAnimation = deathAnims[facingDirection];
            return;
        }

        if (currentState == State.HURT) {
            currentAnimation = hurtAnims[facingDirection];
            hurtTimer -= delta;
            if (hurtTimer <= 0) {
                currentState = State.IDLE;
                isInvincible = false;
            }
            return;
        }

        if (currentState != State.RUN) {
            regenTimer += delta;
            if (regenTimer >= REGEN_DELAY) {
                currentStamina += staminaRegen * delta;
                if (currentStamina > maxStamina) currentStamina = maxStamina;
                if (isExhausted && currentStamina > 25f) isExhausted = false;
            }
        }
    }

    public void updateIdle(float delta) {
        if (!isDead && currentState != State.HURT) {
            currentState = State.IDLE;
        }
        update(delta);

        if (!isDead && currentState == State.IDLE) {
            currentAnimation = idleAnims[facingDirection];
        }
    }

    public void setDirection(String direction) {
        if (isDead || currentState == State.HURT) return;
        switch (direction) {
            case "DOWN": facingDirection = 0; break;
            case "LEFT": facingDirection = 1; break;
            case "RIGHT": facingDirection = 2; break;
            case "UP": facingDirection = 3; break;
        }
    }

    public void move(Vector2 direction, float delta) {
        if (isDead || currentState == State.HURT || isHidden) return;

        boolean isMoving = direction.len2() > 0;
        boolean wantsRun = currentState == State.RUN;

        // Track movement state change for SFX
        wasMovingLastFrame = isCurrentlyMoving;
        isCurrentlyMoving = isMoving;

        if (isMoving) {
            if (Math.abs(direction.x) > Math.abs(direction.y)) {
                facingDirection = (direction.x > 0) ? 2 : 1;
            } else {
                facingDirection = (direction.y > 0) ? 3 : 0;
            }
        }

        float speed = WALK_SPEED;

        if (isMoving) {
            if (wantsRun && currentStamina > 0 && !isExhausted) {
                currentState = State.RUN;
                speed = RUN_SPEED;
                currentStamina -= staminaDrain * delta;
                if (currentStamina <= 0) {
                    currentStamina = 0;
                    isExhausted = true;
                }
                regenTimer = 0;
            } else {
                currentState = State.WALK;
            }
        } else {
            currentState = State.IDLE;
        }

        if (isMoving) {
            position.mulAdd(direction, speed * delta);
        }

        switch (currentState) {
            case RUN: currentAnimation = runAnims[facingDirection]; break;
            case WALK: currentAnimation = walkAnims[facingDirection]; break;
            default:  currentAnimation = idleAnims[facingDirection]; break;
        }
    }

    /**
     * Check if player just started moving (for SFX trigger)
     */
    public boolean justStartedMoving() {
        return isCurrentlyMoving && !wasMovingLastFrame;
    }

    /**
     * Check if player just stopped moving (for SFX stop)
     */
    public boolean justStoppedMoving() {
        return !isCurrentlyMoving && wasMovingLastFrame;
    }

    /**
     * Check if player is currently moving
     */
    public boolean isMoving() {
        return isCurrentlyMoving;
    }

    public void setRunningRequest(boolean isShiftPressed) {
        if (isDead || currentState == State.HURT) return;
        if (isShiftPressed) currentState = State.RUN;
        else if (currentState == State.RUN) currentState = State.WALK;
    }

    public boolean isRunning() {
        return currentState == State.RUN;
    }

    public void render(SpriteBatch batch) {
        if (isHidden) return;

        batch.setColor(Color.WHITE);
        TextureRegion frame = currentAnimation.getKeyFrame(stateTime);
        batch.draw(frame, position.x - 16, position.y - 15);
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x + 11, position.y + 4, 10, 8);
    }
}
