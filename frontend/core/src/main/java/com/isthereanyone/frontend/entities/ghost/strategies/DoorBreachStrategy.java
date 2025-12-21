package com.isthereanyone.frontend.entities.ghost.strategies;

import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public class DoorBreachStrategy implements GhostStrategy {
    private float breachTimer = 1.5f;
    private float targetX, targetY;

    public DoorBreachStrategy(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void executeBehavior(Ghost ghost, Player player, float delta) {
        breachTimer -= delta;

        if (breachTimer <= 0) {
            System.out.println("GHOST: BREACHING DOOR!");

            ghost.setPosition(targetX, targetY);

            ghost.setStrategy(new ChaseStrategy());
        }
    }
}
