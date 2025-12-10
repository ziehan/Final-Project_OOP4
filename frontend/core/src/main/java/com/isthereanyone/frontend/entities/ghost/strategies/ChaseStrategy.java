package com.isthereanyone.frontend.entities.ghost.strategies;

import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public class ChaseStrategy implements GhostStrategy {
    private static final float ESCAPE_RADIUS = 250f;
    private Vector2 direction = new Vector2();

    @Override
    public void executeBehavior(Ghost ghost, Player player, float delta) {
        direction.set(player.position).sub(ghost.getPosition()).nor();

        ghost.getPosition().mulAdd(direction, ghost.getSpeed() * delta);
        ghost.updateAnimationTime(delta);

        float distance = ghost.getPosition().dst(player.position);

        if (distance > ESCAPE_RADIUS) {
            System.out.println("GHOST: Player is missing, back to patrol.");
            ghost.revertToPatrol();
        }
    }
}
