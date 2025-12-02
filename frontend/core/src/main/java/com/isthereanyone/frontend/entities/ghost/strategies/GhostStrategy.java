package com.isthereanyone.frontend.entities.ghost.strategies;

import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;

public interface GhostStrategy {
    void executeBehavior(Ghost ghost, Player player, float delta);
}
