package com.isthereanyone.frontend.screens;

// Game Play Screen - handles main gameplay loop

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.input.InputHandler;
import com.isthereanyone.frontend.managers.GameWorld;
import com.isthereanyone.frontend.managers.LightingSystem;
import com.isthereanyone.frontend.managers.SaveSlotManager;
import com.isthereanyone.frontend.managers.GameSaveManager;
import com.isthereanyone.frontend.screens.states.PlayState;
import com.isthereanyone.frontend.screens.states.RoamingState;
import com.isthereanyone.frontend.ui.PauseButton;
import com.isthereanyone.frontend.ui.PauseMenu;

public class PlayScreen extends BaseScreen {
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    public InputHandler inputHandler;
    public LightingSystem lightingSystem;
    public GameHUD gameHUD;

    public OrthogonalTiledMapRenderer mapRenderer;

    public Viewport uiViewport;
    public Viewport puzzleViewport;
    public SpriteBatch uiBatch;

    private GameWorld world;
    private PlayState currentState;

    private PauseMenu pauseMenu;
    private PauseButton pauseButton;
    private boolean isPaused = false;

    public PlayScreen() {
        super();

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        world = new GameWorld();

        mapRenderer = new OrthogonalTiledMapRenderer(world.map, 1f);

        uiBatch = new SpriteBatch();
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiViewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, uiCamera);

        OrthographicCamera puzzleCamera = new OrthographicCamera();
        puzzleViewport = new FitViewport(450, 450, puzzleCamera);

        lightingSystem = new LightingSystem();
        gameHUD = new GameHUD(uiBatch);

        inputHandler = new InputHandler(this);

        currentState = new RoamingState(this);

        pauseMenu = new PauseMenu(uiViewport);
        pauseButton = new PauseButton(uiViewport);

        pauseButton.setOnClickCallback(() -> togglePause());
        pauseMenu.setOnSaveCallback(() -> saveProgress());
    }

    public GameWorld getWorld() {
        return world;
    }

    public void setState(PlayState newState) {
        this.currentState = newState;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseMenu.show();
        } else {
            pauseMenu.hide();
        }
    }

    private void saveProgress() {
        SaveSlotManager saveManager = SaveSlotManager.getInstance();
        if (saveManager.getCurrentSlot() > 0) {
            // Use GameSaveManager for full save with ghost, tasks, inventory
            GameSaveManager.getInstance().saveGame(saveManager.getCurrentSlot(), world,
                new com.isthereanyone.frontend.network.NetworkCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        System.out.println("[SAVE] Progress saved to backend slot " + saveManager.getCurrentSlot());
                    }

                    @Override
                    public void onFailure(String error) {
                        System.out.println("[SAVE] Backend save failed: " + error + ", saving locally");
                        // Fallback to local save
                        saveManager.saveGame(
                            1,
                            world.player.health,
                            world.player.position.x,
                            world.player.position.y
                        );
                    }
                });
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
        lightingSystem.resize((int)uiViewport.getWorldWidth(), (int)uiViewport.getWorldHeight());
        gameHUD.resize(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isPaused && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = true;
            pauseMenu.show();
        }

        if (isPaused) {
            pauseMenu.update();
            if (!pauseMenu.isVisible()) {
                isPaused = false;
            }
        }

        if (!isPaused) {
            currentState.update(delta);
        }

        currentState.render();

        uiViewport.apply();

        if (!isPaused) {
            pauseButton.update();
            pauseButton.render();
        }

        if (isPaused) {
            pauseMenu.render();
        }
    }

    public void renderHUD() {
        int finishedCount = 0;
        String prompt = null;

        if (world.player.isHidden) {
            prompt = "[E] Leave";
        }
        else {
            for (RectangleMapObject spot : world.hideSpots) {
                Rectangle r = spot.getRectangle();
                float cx = r.x + (r.width / 2);
                float cy = r.y + (r.height / 2);

                if (world.player.position.dst(cx, cy) < 40f) {
                    prompt = "[E] Hide";
                    break;
                }
            }

            if (prompt == null) {
                for (BaseTask task : world.tasks) {
                    if (task.isCompleted) finishedCount++;
                    else if (world.player.position.dst(task.getBounds().x, task.getBounds().y) < 30f) {
                        prompt = "[E] Interact";
                    }
                }
            }

            if (prompt == null) {
                for (RitualItem item : world.itemsOnGround) {
                    if (!item.isCollected && world.player.position.dst(item.getBounds().x, item.getBounds().y) < 40f) {
                        prompt = "[E] Pick Up " + item.getType();
                        break;
                    }
                }
            }

            if (prompt == null && world.player.position.dst(world.gate.getBounds().x, world.gate.getBounds().y) < 80f) {
                prompt = world.gate.isLocked() ? "GATE LOCKED" : "[E] ESCAPE!";
            }
        }

        gameHUD.render(world.player, finishedCount, world.tasks.size, prompt);
    }

    public void renderWorld() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        uiBatch.dispose();
        lightingSystem.dispose();
        gameHUD.dispose();
        world.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (pauseMenu != null) pauseMenu.dispose();
        if (pauseButton != null) pauseButton.dispose();
    }
}
