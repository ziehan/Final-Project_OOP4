package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.input.InputHandler;
import com.isthereanyone.frontend.managers.GameWorld;
import com.isthereanyone.frontend.managers.LightingSystem;
import com.isthereanyone.frontend.screens.states.PlayState;
import com.isthereanyone.frontend.screens.states.RoamingState;

public class PlayScreen extends BaseScreen {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public InputHandler inputHandler;
    public LightingSystem lightingSystem;
    public GameHUD gameHUD;

    public Viewport uiViewport;
    public Viewport puzzleViewport;
    public SpriteBatch uiBatch;

    private GameWorld world;

    private PlayState currentState;

    public PlayScreen() {
        super();

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        world = new GameWorld();

        uiBatch = new SpriteBatch();
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiViewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, uiCamera);

        OrthographicCamera puzzleCamera = new OrthographicCamera();
        puzzleViewport = new FitViewport(450, 450, puzzleCamera);

        lightingSystem = new LightingSystem();
        gameHUD = new GameHUD(uiBatch);

        inputHandler = new InputHandler(this);

        currentState = new RoamingState(this);
    }

    public GameWorld getWorld() {
        return world;
    }

    public void setState(PlayState newState) {
        this.currentState = newState;
    }

    @Override
    public void resize(int width, int height) {
        lightingSystem.resize((int)uiViewport.getWorldWidth(), (int)uiViewport.getWorldHeight());
        gameHUD.resize(width, height);
    }

    @Override
    public void render(float delta) {
        lightingSystem.renderLightMap(batch, world.player, camera);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        currentState.update(delta);
        currentState.render();
    }


    public void renderWorld() {
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        world.render(batch, shapeRenderer);
    }

    public void renderHUD() {
        int finishedCount = 0;
        String prompt = null;

        for (BaseTask task : world.tasks) {
            if (task.isCompleted) finishedCount++;
            else if (world.player.position.dst(task.getBounds().x, task.getBounds().y) < 60f) {
                prompt = "[E] Interact";
            }
        }
        for (RitualItem item : world.itemsOnGround) {
            if (!item.isCollected && world.player.position.dst(item.getBounds().x, item.getBounds().y) < 40f) {
                prompt = "[E] Pick Up " + item.getType();
                break;
            }
        }
        if (world.player.position.dst(world.gate.getBounds().x, world.gate.getBounds().y) < 80f) {
            prompt = world.gate.isLocked() ? "GATE LOCKED" : "[E] ESCAPE!";
        }

        gameHUD.render(world.player, finishedCount, world.tasks.size, prompt);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        uiBatch.dispose();
        lightingSystem.dispose();
        gameHUD.dispose();
        world.dispose();
    }
}
