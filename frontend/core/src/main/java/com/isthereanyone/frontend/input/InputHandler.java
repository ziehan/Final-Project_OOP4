package com.isthereanyone.frontend.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.input.commands.MoveCommand;

public class InputHandler {
    private MoveCommand moveCommand;
    private Vector2 direction = new Vector2();

    public InputHandler() {
        moveCommand = new MoveCommand();
    }

    public void handleInput(Player player, float delta) {
        direction.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x = 1;

        if (direction.len2() > 0) {
            direction.nor();

            String dirName = getDirectionName(direction);

            moveCommand.setDirectionVector(direction);
            moveCommand.setDirectionName(dirName);
            moveCommand.execute(player, delta);
        } else {
            player.setIdle();
        }
    }

    private String getDirectionName(Vector2 dir) {
        if (Math.abs(dir.x) > 0.1f) {
            return dir.x > 0 ? "RIGHT" : "LEFT";
        } else {
            return dir.y > 0 ? "UP" : "DOWN";
        }
    }
}
