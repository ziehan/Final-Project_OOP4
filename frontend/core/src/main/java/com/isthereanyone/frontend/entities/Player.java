package com.isthereanyone.frontend.entities;

public class Player {
<<<<<<< Updated upstream
=======
    public Vector2 position;
    private float speed = 100f;

    private Animation<TextureRegion> walkDown, walkUp, walkLeft, walkRight;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    private boolean isMoving = false;

    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;

    public Player(float startX, float startY) {
        position = new Vector2(startX, startY);

        Texture sheet = MyAssetManager.getInstance().get("she.png");
        TextureRegion[][] tmp = TextureRegion.split(sheet, FRAME_WIDTH, FRAME_HEIGHT);

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
>>>>>>> Stashed changes
}
