package com.isthereanyone.frontend.entities.ghost.strategies;

import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public class PatrolStrategy implements GhostStrategy {
    private float timer = 0;
    private int moveDirX = 1;

    private static final float DETECTION_RADIUS = 150f;

    @Override
    public void executeBehavior(Ghost ghost, Player player, float delta) {
        float patrolSpeed = ghost.getSpeed() * 0.4f;

        ghost.getPosition().x += moveDirX * patrolSpeed * delta;

        timer += delta;
        if (timer > 3f) {
            moveDirX *= -1;
            timer = 0;
        }

        float distance = ghost.getPosition().dst(player.position);

        if (distance < DETECTION_RADIUS) {
            System.out.println("GHOST: Player found, change to chase");
            ghost.setStrategy(new ChaseStrategy());
        }
    }
}
