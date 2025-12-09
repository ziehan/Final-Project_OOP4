package com.isthereanyone.frontend.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.input.commands.*;
import com.isthereanyone.frontend.screens.PlayScreen;

public class InputHandler {
    private Command moveCommand;
    private Command interactCommand;
    private Command dropCommand;
    private Command selectSlot1, selectSlot2, selectSlot3;

    private Vector2 direction = new Vector2();

    public InputHandler(PlayScreen screen) {
        moveCommand = new MoveCommand();
        interactCommand = new InteractCommand(screen);
        dropCommand = new DropCommand(screen);

        selectSlot1 = new SelectSlotCommand(0);
        selectSlot2 = new SelectSlotCommand(1);
        selectSlot3 = new SelectSlotCommand(2);
    }

    public void handleInput(Player player, float delta) {
        direction.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) direction.y = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) direction.y = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) direction.x = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) direction.x = 1;
        player.setRunning(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT));

        if (direction.len2() > 0) {
            direction.nor();
            ((MoveCommand)moveCommand).setDirectionVector(direction);
            ((MoveCommand)moveCommand).setDirectionName(getDirectionName(direction));
            moveCommand.execute(player, delta);
        } else {
            player.updateIdle(delta);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) interactCommand.execute(player, delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.G)) dropCommand.execute(player, delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) selectSlot1.execute(player, delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) selectSlot2.execute(player, delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) selectSlot3.execute(player, delta);
    }

    private String getDirectionName(Vector2 dir) {
        if (Math.abs(dir.x) > 0.1f) return dir.x > 0 ? "RIGHT" : "LEFT";
        else return dir.y > 0 ? "UP" : "DOWN";
    }
}
