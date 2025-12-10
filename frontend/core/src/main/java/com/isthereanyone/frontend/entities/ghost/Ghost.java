package com.isthereanyone.frontend.entities.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.strategies.ChaseStrategy;
import com.isthereanyone.frontend.entities.ghost.strategies.GhostStrategy;
import com.isthereanyone.frontend.entities.ghost.strategies.PatrolStrategy;
import com.isthereanyone.frontend.entities.ghost.strategies.InvestigateStrategy;
import com.isthereanyone.frontend.entities.ghost.strategies.WaypointStrategy;
import com.isthereanyone.frontend.managers.MyAssetManager;
import com.isthereanyone.frontend.observer.GameObserver;

public class Ghost implements GameObserver {
    public enum State { IDLE, WALK, RUN, ATTACK }
    private State currentState = State.IDLE;

    private Vector2 position;
    private GhostStrategy currentStrategy;

    public Vector2 investigationTarget = null;
    private float speedMultiplier = 1.0f;
    private float baseSpeed = 100f;
    private Array<Vector2> patrolPoints;

    private float attackCooldown = 0f;
    private boolean isCoolingDown = false;
    private float attackAnimTimer = 0f;

    private Animation<TextureRegion>[] idleAnims;
    private Animation<TextureRegion>[] walkAnims;
    private Animation<TextureRegion>[] runAnims;
    private Animation<TextureRegion>[] attackAnims;

    private Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;
    private Vector2 lastPosition = new Vector2();
    private int facingDirection = 0;

    public Ghost(float x, float y) {
        this.position = new Vector2(x, y);
        this.lastPosition.set(x, y);

        idleAnims = loadAnimation("Vampires3_Idle_with_shadow.png", 4, 0.15f);
        walkAnims = loadAnimation("Vampires3_Walk_with_shadow.png", 6, 0.15f);
        runAnims  = loadAnimation("Vampires3_Run_with_shadow.png", 8, 0.1f);

        attackAnims = loadAnimation("Vampires3_Attack_with_shadow.png", 12, 0.1f);
        for (Animation anim : attackAnims) {
            anim.setPlayMode(Animation.PlayMode.NORMAL);
        }

        currentAnimation = idleAnims[0];
        this.currentStrategy = new PatrolStrategy();
    }

    @SuppressWarnings("unchecked")
    private Animation<TextureRegion>[] loadAnimation(String fileName, int frameCount, float frameDuration) {
        Texture sheet = MyAssetManager.getInstance().get(fileName);
        int frameWidth = sheet.getWidth() / frameCount;
        int frameHeight = sheet.getHeight() / 4;

        TextureRegion[][] tmp = TextureRegion.split(sheet, frameWidth, frameHeight);
        Animation<TextureRegion>[] anims = new Animation[4];

        anims[0] = new Animation<>(frameDuration, tmp[0]);
        anims[1] = new Animation<>(frameDuration, tmp[1]);
        anims[2] = new Animation<>(frameDuration, tmp[2]);
        anims[3] = new Animation<>(frameDuration, tmp[3]);

        for (Animation a : anims) a.setPlayMode(Animation.PlayMode.LOOP);
        return anims;
    }

    public void setPatrolPoints(Array<Vector2> points) {
        this.patrolPoints = points;
        this.currentStrategy = new WaypointStrategy(this.patrolPoints);
    }

    public void revertToPatrol() {
        if (patrolPoints != null) {
            this.currentStrategy = new WaypointStrategy(this.patrolPoints);
        } else {
            this.currentStrategy = new PatrolStrategy();
        }
    }

    public void update(Player player, float delta) {
        stateTime += delta;

        if (isCoolingDown) {
            attackCooldown -= delta;
            attackAnimTimer += delta;

            if (attackAnims[facingDirection].isAnimationFinished(attackAnimTimer)) {
                currentState = State.IDLE;
            } else {
                currentState = State.ATTACK;
            }

            if (attackCooldown <= 0) {
                isCoolingDown = false;
                System.out.println("GHOST: Recharge completed");
            } else {
                updateFacingDirection(player.position.x - position.x, player.position.y - position.y);
                updateAnimation();
                return;
            }
        }

        lastPosition.set(position.x, position.y);

        currentStrategy.executeBehavior(this, player, delta);

        float moveX = position.x - lastPosition.x;
        float moveY = position.y - lastPosition.y;

        if (Math.abs(moveX) > 0 || Math.abs(moveY) > 0) {
            if (currentStrategy instanceof ChaseStrategy) {
                currentState = State.RUN;
            } else {
                currentState = State.WALK;
            }
            updateFacingDirection(moveX, moveY);
        } else {
            currentState = State.IDLE;
        }

        updateAnimation();
    }

    private void updateFacingDirection(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            facingDirection = (dx > 0) ? 3 : 2;
        } else if (Math.abs(dy) > 0.1f) {
            facingDirection = (dy > 0) ? 1 : 0;
        }
    }

    private void updateAnimation() {
        switch (currentState) {
            case ATTACK: currentAnimation = attackAnims[facingDirection]; break;
            case RUN:    currentAnimation = runAnims[facingDirection]; break;
            case WALK:   currentAnimation = walkAnims[facingDirection]; break;
            default:     currentAnimation = idleAnims[facingDirection]; break;
        }
    }

    public void render(SpriteBatch batch) {
        batch.setColor(Color.WHITE);

        float timerToUse = (currentState == State.ATTACK) ? attackAnimTimer : stateTime;

        TextureRegion currentFrame = currentAnimation.getKeyFrame(timerToUse);
        batch.draw(currentFrame, position.x - 16, position.y - 10);
    }

    public void setStrategy(GhostStrategy strategy) {
        this.currentStrategy = strategy;
    }

    public Vector2 getPosition() { return position; }
    public void setPosition(float x, float y) { this.position.set(x, y); }
    public float getSpeedMultiplier() { return speedMultiplier; }
    public float getSpeed() { return baseSpeed * speedMultiplier; }
    public void updateAnimationTime(float delta) {}

    @Override
    public void onTaskCompleted(int totalTasksFinished) {
        speedMultiplier += 0.2f;
    }

    @Override
    public void onAllTasksCompleted() {
        speedMultiplier = 2.5f;
        setStrategy(new ChaseStrategy());
    }

    @Override
    public void onSoundEmitted(float x, float y, float radius) {
        float dist = position.dst(x, y);
        if (dist <= radius && !(currentStrategy instanceof ChaseStrategy)) {
            System.out.println("GHOST: Investigating");
            this.investigationTarget = new Vector2(x, y);
            setStrategy(new InvestigateStrategy());
        }
    }

    public void triggerAttackCooldown() {
        if (isCoolingDown) return;
        isCoolingDown = true;
        attackCooldown = 3.0f;
        attackAnimTimer = 0f;
        currentState = State.ATTACK;
        System.out.println("GHOST: Recharging");
    }

    public boolean isCoolingDown() {
        return isCoolingDown;
    }
}
