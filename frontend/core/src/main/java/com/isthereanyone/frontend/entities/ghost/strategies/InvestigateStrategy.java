package com.isthereanyone.frontend.entities.ghost.strategies;

import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public class InvestigateStrategy implements GhostStrategy {
    private float searchTimer = 3.0f;
    private boolean arrived = false;

    @Override
    public void executeBehavior(Ghost ghost, Player player, float delta) {
        if (ghost.getPosition().dst(player.position) < 150f) {
            System.out.println("GHOST: Switch to Chase");
            ghost.setStrategy(new ChaseStrategy());
            return;
        }

        Vector2 target = ghost.investigationTarget;
        if (target == null) {
            ghost.revertToPatrol();
            return;
        }

        float dist = ghost.getPosition().dst(target);

        if (dist < 10f) {
            arrived = true;
        } else if (!arrived) {
            Vector2 direction = new Vector2(target).sub(ghost.getPosition()).nor();
            float speed = 90f * ghost.getSpeedMultiplier() * delta;
            ghost.getPosition().mulAdd(direction, speed);
        }

        if (arrived) {
            searchTimer -= delta;
            if (searchTimer <= 0) {
                System.out.println("GHOST: Back to patrol");
                ghost.revertToPatrol();
            }
        }
    }
}
