package com.isthereanyone.frontend.input.commands;

import com.isthereanyone.frontend.entities.Player;

public class MoveCommand implements Command {
    private float dirX, dirY;
    private String directionName; // "UP", "DOWN", "LEFT", "RIGHT"

    public MoveCommand(float dirX, float dirY, String directionName) {
        this.dirX = dirX;
        this.dirY = dirY;
        this.directionName = directionName;
    }

    @Override
    public void execute(Player player, float delta) {
        player.move(dirX, dirY, delta);
        player.setDirection(directionName);
    }
}

