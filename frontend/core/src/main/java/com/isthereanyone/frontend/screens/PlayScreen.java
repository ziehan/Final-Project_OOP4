package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
<<<<<<< Updated upstream

public class PlayScreen extends BaseScreen{
    public PlayScreen(){
        super();
=======
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;
import com.isthereanyone.frontend.observer.EventManager;
import com.isthereanyone.frontend.tasks.BaseTask;
import com.isthereanyone.frontend.tasks.TaskFactory;
import com.isthereanyone.frontend.input.InputHandler;

public class PlayScreen extends BaseScreen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Player player;
    private Ghost ghost;
    private InputHandler inputHandler;

    private Array<BaseTask> tasks;

    public PlayScreen() {
        super();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        inputHandler = new InputHandler();
        player = new Player(100, 100);
        ghost = new Ghost(400, 100);

        EventManager.getInstance().addObserver(ghost);

        tasks = new Array<>();
        tasks.add(TaskFactory.createTask("WIRE", 200, 200));
        tasks.add(TaskFactory.createTask("RITUAL", 500, 300));
        tasks.add(TaskFactory.createTask("WIRE", 100, 300));
>>>>>>> Stashed changes
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
<<<<<<< Updated upstream
=======

        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();

        inputHandler.handleInput(player, delta);
        ghost.update(player, delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            for (BaseTask task : tasks) {
                task.interact(player);
            }
        }

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BaseTask task : tasks) {
            task.render(shapeRenderer);
        }
        shapeRenderer.end();

        batch.begin();
        player.render(batch);
        ghost.render(batch);
        batch.end();
>>>>>>> Stashed changes
    }

}
