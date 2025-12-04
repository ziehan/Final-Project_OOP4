package com.isthereanyone.frontend.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.managers.MyAssetManager;

public class Player {
    public Vector2 position;
    private float speed = 100f;
    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean isMoving = false;

    public Player(float startX, float startY) {
        position = new Vector2(startX, startY);
        Texture sheet = MyAssetManager.getInstance().get("she.png");
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);

        walkDown  = new Animation<>(0.2f, tmp[0]);
        walkUp    = new Animation<>(0.2f, tmp[1]);
        walkLeft  = new Animation<>(0.2f, tmp[2]);
        walkRight = new Animation<>(0.2f, tmp[3]);

        currentAnimation = walkDown;
        stateTime = 0f;
    }

    public void move(Vector2 direction, float delta) {
        position.x += direction.x * speed * delta;
        position.y += direction.y * speed * delta;
        stateTime += delta;
        isMoving = true;
    }

    public void setIdle() {
        isMoving = false;
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
        TextureRegion currentFrame;
        if (isMoving) {
            currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        } else {
            currentFrame = currentAnimation.getKeyFrames()[0];
        }
        batch.draw(currentFrame, position.x, position.y);
    }
}
