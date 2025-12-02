package com.isthereanyone.frontend.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.input.commands.Command;
import com.isthereanyone.frontend.input.commands.MoveCommand;

public class InputHandler {
    private Command moveUp, moveDown, moveLeft, moveRight;

    public InputHandler() {
        moveUp = new MoveCommand(0, 1, "UP");
        moveDown = new MoveCommand(0, -1, "DOWN");
        moveLeft = new MoveCommand(-1, 0, "LEFT");
        moveRight = new MoveCommand(1, 0, "RIGHT");
    }

    public void handleInput(Player player, float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveUp.execute(player, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDown.execute(player, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveLeft.execute(player, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveRight.execute(player, delta);
    }
}
