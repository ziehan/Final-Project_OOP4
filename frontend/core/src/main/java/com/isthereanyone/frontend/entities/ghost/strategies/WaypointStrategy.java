package com.isthereanyone.frontend.entities.ghost.strategies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public class WaypointStrategy implements GhostStrategy {
    private Array<Vector2> waypoints;
    private int currentIndex = 0;
    private float waitTimer = 0f;
    private boolean isWaiting = false;

    public WaypointStrategy(Array<Vector2> points) {
        this.waypoints = points;
    }

    @Override
    public void executeBehavior(Ghost ghost, Player player, float delta) {
        if (waypoints == null || waypoints.size == 0) return;

        if (ghost.getPosition().dst(player.position) < 150f) {
            System.out.println("GHOST: Switch to Chase");
            ghost.setStrategy(new ChaseStrategy());
            return;
        }

        Vector2 target = waypoints.get(currentIndex);
        float dist = ghost.getPosition().dst(target);

        if (dist < 10f) {
            if (!isWaiting) {
                isWaiting = true;
                waitTimer = 2.0f;
            } else {
                waitTimer -= delta;
                if (waitTimer <= 0) {
                    isWaiting = false;
                    currentIndex = (currentIndex + 1) % waypoints.size;
                }
            }
        } else {
            Vector2 direction = new Vector2(target).sub(ghost.getPosition()).nor();
            float speed = 60f * ghost.getSpeedMultiplier() * delta;
            ghost.getPosition().mulAdd(direction, speed);
        }
    }
}
