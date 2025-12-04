package com.isthereanyone.frontend.input.commands;

import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;

public class MoveCommand implements Command {
    private Vector2 directionVector = new Vector2();
    private String directionName;

    public MoveCommand() {}

    public void setDirectionVector(Vector2 directionVector){
        this.directionVector.set(directionVector);
    }

    public void setDirectionName(String directionName){

    @Override
    public void execute(Player player, float delta) {
        player.move(directionVector, delta);
        player.setDirection(directionName);
    }
}

