package com.isthereanyone.frontend.entities.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.strategies.GhostStrategy;
import com.isthereanyone.frontend.entities.ghost.strategies.PatrolStrategy;
import com.isthereanyone.frontend.managers.MyAssetManager;
import com.isthereanyone.frontend.observer.GameObserver;

public class Ghost implements GameObserver {
    private Vector2 position;
    private float speed = 90f;
    private GhostStrategy currentStrategy;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private float speedMultiplier = 1.0f;

    public Ghost(float startX, float startY) {
        this.position = new Vector2(startX, startY);

        this.currentStrategy = new PatrolStrategy();

        Texture sheet = MyAssetManager.getInstance().get("she.png");
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);
        walkAnimation = new Animation<>(0.1f, tmp[0]);
        stateTime = 0f;
    }

    public void update(Player player, float delta) {
        currentStrategy.executeBehavior(this, player, delta);
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = walkAnimation.getKeyFrame(stateTime, true);

        batch.setColor(Color.RED);
        batch.draw(currentFrame, position.x, position.y);
        batch.setColor(Color.WHITE);
    }

    public void setStrategy(GhostStrategy strategy) {
        this.currentStrategy = strategy;
    }

    public Vector2 getPosition() { return position; }
    public float getSpeed() { return speed * speedMultiplier; }

    public void updateAnimationTime(float delta) {
        stateTime += delta;
    }

    @Override
    public void onTaskCompleted(int totalTaskFinished) {
        speedMultiplier += 0.2f;
        System.out.println("GHOST: GRRR!! SPEED MENINGKAT JADI " + (speed * speedMultiplier));
    }
}
