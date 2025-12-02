package com.isthereanyone.frontend.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.managers.MyAssetManager;

public class Player {
    public Vector2 position;
    private float speed = 100f; // Kecepatan gerak

    // Animasi
    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    private static final int FRAME_WIDTH = 32; // Sesuaikan dengan ukuran sprite (128/4)
    private static final int FRAME_HEIGHT = 32;

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

    public void move(float dirX, float dirY, float delta) {
        position.x += dirX * speed * delta;
        position.y += dirY * speed * delta;
        stateTime += delta; // Update timer animasi saat bergerak
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

