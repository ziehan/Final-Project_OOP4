package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
<<<<<<< Updated upstream
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
=======
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
>>>>>>> Stashed changes
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;
import com.isthereanyone.frontend.input.InputHandler;
<<<<<<< Updated upstream
import com.isthereanyone.frontend.tasks.BaseTask;
import com.isthereanyone.frontend.tasks.TaskFactory;
=======
import com.isthereanyone.frontend.managers.LightingSystem;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.observer.EventManager;
>>>>>>> Stashed changes

public class PlayScreen extends BaseScreen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;


    private Player player;
    private Ghost ghost;
<<<<<<< Updated upstream
    private BaseTask task1;
    private InputHandler inputHandler;
=======
    private Array<BaseTask> tasks;
    private InputHandler inputHandler;
    private LightingSystem lightingSystem;
    private GameHUD gameHUD;
    private SpriteBatch uiBatch;
    private Viewport uiViewport;
>>>>>>> Stashed changes

    public PlayScreen() {
        super();
        
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        inputHandler = new InputHandler();


        player = new Player(100, 100);
        ghost = new Ghost(300, 300);


<<<<<<< Updated upstream
        task1 = TaskFactory.createTask("SIMPLE", 150, 150);
=======
        uiBatch = new SpriteBatch();
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiViewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, uiCamera);

        lightingSystem = new LightingSystem();
        gameHUD = new GameHUD(uiBatch);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);

        uiViewport.update(width, height, true);

        lightingSystem.resize((int)uiViewport.getWorldWidth(), (int)uiViewport.getWorldHeight());

        gameHUD.resize(width, height);
>>>>>>> Stashed changes
    }

    @Override
    public void render(float delta) {
<<<<<<< Updated upstream
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
=======
        lightingSystem.renderLightMap(batch, player, camera);

        Gdx.gl.glClearColor(0, 0, 0, 1);
>>>>>>> Stashed changes
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.position.set(player.position.x, player.position.y, 0);
        camera.update();


        inputHandler.handleInput(player, delta);

<<<<<<< Updated upstream

        ghost.update(player, delta);

=======
        if (player.position.x < 0) player.position.x = 0;
        if (player.position.x > 1000) player.position.x = 1000; // Agak diperlebar
        if (player.position.y < 0) player.position.y = 0;
        if (player.position.y > 1000) player.position.y = 1000;

        ghost.update(player, delta);

        if (ghost.getPosition().dst(player.position) < 20f) {
            ScreenManager.getInstance().setScreen(new GameOverScreen());
        }
>>>>>>> Stashed changes

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            task1.interact(player);
        }

<<<<<<< Updated upstream
=======
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(-500, -500, 2000, 2000);

        for (BaseTask task : tasks) task.render(shapeRenderer);
        shapeRenderer.end();
>>>>>>> Stashed changes

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);


        batch.begin();
        player.render(batch);
        batch.end();

<<<<<<< Updated upstream

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        ghost.render(shapeRenderer);
        task1.render(shapeRenderer);
        shapeRenderer.end();
=======
        lightingSystem.renderDarkness(uiBatch, uiViewport);

        int finishedCount = 0;
        String prompt = null;
        for (BaseTask task : tasks) {
            if (task.isCompleted) finishedCount++;
            else if (player.position.dst(task.getBounds().x, task.getBounds().y) < 40f) {
                prompt = "[E] Interact";
            }
        }
        gameHUD.render(player, finishedCount, tasks.size, prompt);

        viewport.apply();
>>>>>>> Stashed changes
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
<<<<<<< Updated upstream
=======
        uiBatch.dispose();
        lightingSystem.dispose();
        gameHUD.dispose();
>>>>>>> Stashed changes
    }
}

