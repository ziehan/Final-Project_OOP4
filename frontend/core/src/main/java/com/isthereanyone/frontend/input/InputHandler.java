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
<<<<<<< Updated upstream
        if (Gdx.input.isKeyPressed(Input.Keys.W)) moveUp.execute(player, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) moveDown.execute(player, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) moveLeft.execute(player, delta);
        if (Gdx.input.isKeyPressed(Input.Keys.D)) moveRight.execute(player, delta);
=======
        direction.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x = 1;

        boolean isShiftPressed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
        player.setRunning(isShiftPressed);

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
>>>>>>> Stashed changes
    }
}
