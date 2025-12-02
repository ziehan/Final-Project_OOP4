package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;
import com.isthereanyone.frontend.input.InputHandler;
import com.isthereanyone.frontend.tasks.BaseTask;
import com.isthereanyone.frontend.tasks.TaskFactory;

public class PlayScreen extends BaseScreen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;


    private Player player;
    private Ghost ghost;
    private BaseTask task1;
    private InputHandler inputHandler;

    public PlayScreen() {
        super();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        inputHandler = new InputHandler();


        player = new Player(100, 100);
        ghost = new Ghost(300, 300);


        task1 = TaskFactory.createTask("SIMPLE", 150, 150);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();


        inputHandler.handleInput(player, delta);


        ghost.update(player, delta);


        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            task1.interact(player);
        }


        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);


        batch.begin();
        player.render(batch);
        batch.end();


        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ghost.render(shapeRenderer);
        task1.render(shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
    }
}
