package com.isthereanyone.frontend.entities.ghost.strategies;

import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public class PatrolStrategy implements GhostStrategy{
    private float timer = 0f;
    private int moveDir = 0;

    @Override
    public void executeBehavior(Ghost ghost, Player player, float delta) {
        ghost.position.x += 30f * moveDir * delta;

        timer += delta;
        if(timer > 2f){
            moveDir *= -1;
            timer = 0;
        }

        float distance = ghost.position.dst(player.position);
        if(distance < 100f){
            System.out.println("PLAYER SPOTTED! SWITCHING TO CHASE!");
            ghost.setCurrentStrategy(new ChaseStartegy());
        }
    }
}
