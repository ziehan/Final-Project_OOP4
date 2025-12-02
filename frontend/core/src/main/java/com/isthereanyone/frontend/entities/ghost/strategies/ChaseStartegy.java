package com.isthereanyone.frontend.entities.ghost.strategies;

import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public class ChaseStartegy implements GhostStrategy{
    @Override
    public void executeBehavior(Ghost ghost, Player player, float delta) {
        Vector2 direction = new Vector2(player.position).sub(ghost.position).nor();
        ghost.position.mulAdd(direction, 70f * delta);

        if(ghost.position.dst(player.position) > 200f){
            System.out.println("Lost player... returning to patrol.");
            ghost.setCurrentStrategy(new PatrolStrategy());
        }
    }
}
