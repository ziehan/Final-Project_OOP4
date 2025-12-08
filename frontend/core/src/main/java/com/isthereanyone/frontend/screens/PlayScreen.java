package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;
import com.isthereanyone.frontend.entities.items.ItemType;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.entities.tasks.TaskFactory;
import com.isthereanyone.frontend.input.InputHandler;
import com.isthereanyone.frontend.managers.LightingSystem;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.observer.EventManager;

public class PlayScreen extends BaseScreen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    private Player player;
    private Ghost ghost;
    private Array<BaseTask> tasks;
    private Array<RitualItem> itemsOnGround;

    private InputHandler inputHandler;
    private LightingSystem lightingSystem;
    private GameHUD gameHUD;

    private Viewport uiViewport;
    private Viewport puzzleViewport;
    private SpriteBatch uiBatch;

    private BaseTask activeTask = null;

    public PlayScreen() {
        super();

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        inputHandler = new InputHandler();

        player = new Player(100, 100);
        ghost = new Ghost(400, 100);
        EventManager.getInstance().addObserver(ghost);

        tasks = new Array<>();
        tasks.add(TaskFactory.createTask("STONE", 200, 200));
        tasks.add(TaskFactory.createTask("GENERATOR", 500, 300));
        tasks.add(TaskFactory.createTask("RITUAL", 800, 500));

        uiBatch = new SpriteBatch();
        OrthographicCamera uiCamera = new OrthographicCamera();
        uiViewport = new ExtendViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, uiCamera);

        OrthographicCamera puzzleCamera = new OrthographicCamera();
        puzzleViewport = new FitViewport(450, 450, puzzleCamera);

        lightingSystem = new LightingSystem();
        gameHUD = new GameHUD(uiBatch);

        spawnItems();
    }

    private void spawnItems() {
        itemsOnGround = new Array<>();

        Array<ItemType> itemPool = new Array<>();
        for (ItemType type : ItemType.values()) itemPool.add(type);
        itemPool.shuffle();

        float altarX = 800;
        float altarY = 500;

        for (ItemType type : itemPool) {
            boolean valid = false;
            int attempts = 0;
            float x = 0, y = 0;

            while (!valid && attempts < 50) {
                attempts++;
                x = MathUtils.random(50, 950);
                y = MathUtils.random(50, 950);

                if (Vector2.dst(x, y, altarX, altarY) < 250f) continue;

                boolean tooClose = false;
                for (RitualItem existing : itemsOnGround) {
                    if (Vector2.dst(x, y, existing.getBounds().x, existing.getBounds().y) < 150f) {
                        tooClose = true;
                        break;
                    }
                }
                if (!tooClose) valid = true;
            }
            itemsOnGround.add(new RitualItem(type, x, y));
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
        lightingSystem.renderLightMap(batch, player, camera);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (activeTask != null) {
            renderMinigameMode(delta);
        } else {
            renderNormalMode(delta);
        }
    }

    private void renderNormalMode(float delta) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();

        viewport.update(screenW, screenH, false);
        viewport.setScreenBounds(0, 0, screenW, screenH);
        viewport.apply();

        camera.zoom = 1.0f;
        camera.position.set(player.position.x + 16, player.position.y + 16, 0);
        camera.update();

        updateGameLogic(delta, true);

        renderWorld();

        uiViewport.update(screenW, screenH, true);
        uiViewport.setScreenBounds(0, 0, screenW, screenH);
        lightingSystem.renderDarkness(uiBatch, uiViewport);

        renderHUD();
    }

    private void renderMinigameMode(float delta) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int halfW = screenW / 2;

        viewport.update(halfW, screenH, false);
        viewport.setScreenBounds(0, 0, halfW, screenH);
        viewport.apply();

        camera.zoom = 0.5f;
        camera.position.set(player.position.x + 16, player.position.y + 16, 0);
        camera.update();

        updateGameLogic(delta, false);
        renderWorld();

        uiViewport.update(halfW, screenH, true);
        uiViewport.setScreenBounds(0, 0, halfW, screenH);
        lightingSystem.renderDarkness(uiBatch, uiViewport);

        int margin = 10;
        int maxH = screenH - (2 * margin);
        int maxW = halfW - margin;
        int squareSize = Math.min(maxH, maxW);

        int puzzleX = screenW - squareSize - margin;
        int puzzleY = (screenH - squareSize) / 2;

        puzzleViewport.update(squareSize, squareSize, true);
        puzzleViewport.setScreenBounds(puzzleX, puzzleY, squareSize, squareSize);
        puzzleViewport.apply();

        boolean shouldClose = activeTask.updateMinigame(delta, puzzleViewport, player);
        if (shouldClose) {
            if (activeTask.isCompleted) System.out.println("Task Selesai!");
            activeTask = null;
            return;
        }

        shapeRenderer.setProjectionMatrix(puzzleViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(
            puzzleViewport.getCamera().position.x - puzzleViewport.getWorldWidth()/2,
            puzzleViewport.getCamera().position.y - puzzleViewport.getWorldHeight()/2,
            puzzleViewport.getWorldWidth(),
            puzzleViewport.getWorldHeight()
        );
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(
            puzzleViewport.getCamera().position.x - puzzleViewport.getWorldWidth()/2,
            puzzleViewport.getCamera().position.y - puzzleViewport.getWorldHeight()/2,
            puzzleViewport.getWorldWidth(),
            puzzleViewport.getWorldHeight()
        );
        shapeRenderer.end();

        activeTask.renderMinigame(batch, shapeRenderer, puzzleViewport);
    }

    private void renderWorld() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(-1000, -1000, 4000, 4000);

        for (BaseTask task : tasks) task.render(shapeRenderer);

        for (RitualItem item : itemsOnGround) item.render(batch, shapeRenderer);

        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        ghost.render(batch);
        batch.end();
    }

    private void renderHUD() {
        int finishedCount = 0;
        String prompt = null;
        for (BaseTask task : tasks) {
            if (task.isCompleted) finishedCount++;
            else if (player.position.dst(task.getBounds().x, task.getBounds().y) < 60f) {
                prompt = "[E] Interact";
            }
        }

        for (RitualItem item : itemsOnGround) {
            if (!item.isCollected && player.position.dst(item.getBounds().x, item.getBounds().y) < 40f) {
                prompt = "[E] Pick Up " + item.getType();
                break;
            }
        }

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        uiViewport.update(screenW, screenH, true);
        uiViewport.setScreenBounds(0, 0, screenW, screenH);

        gameHUD.render(player, finishedCount, tasks.size, prompt);
    }

    private void updateGameLogic(float delta, boolean processInput) {
        if (processInput) {
            inputHandler.handleInput(player, delta);

            if (player.position.x < 0) player.position.x = 0;
            if (player.position.x > 1000) player.position.x = 1000;
            if (player.position.y < 0) player.position.y = 0;
            if (player.position.y > 1000) player.position.y = 1000;

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) player.inventory.setSelectedSlot(0);
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) player.inventory.setSelectedSlot(1);
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) player.inventory.setSelectedSlot(2);

            if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
                ItemType droppedType = player.inventory.dropSelectedItem();
                if (droppedType != null) {
                    System.out.println("Dropped: " + droppedType);
                    itemsOnGround.add(new RitualItem(droppedType, player.position.x + 20, player.position.y));
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                boolean pickedItem = false;
                for (RitualItem item : itemsOnGround) {
                    if (!item.isCollected && player.position.dst(item.getBounds().x, item.getBounds().y) < 40f) {
                        if (!player.inventory.isFull()) {
                            item.isCollected = true;
                            player.inventory.addItem(item.getType());
                            itemsOnGround.removeValue(item, true);
                        } else {
                            // Swap
                            ItemType itemToDrop = player.inventory.swapItem(item.getType());
                            if (itemToDrop != null) {
                                RitualItem newItemOnGround = new RitualItem(itemToDrop, item.getBounds().x, item.getBounds().y);
                                itemsOnGround.add(newItemOnGround);
                                item.isCollected = true;
                                itemsOnGround.removeValue(item, true);
                            }
                        }
                        pickedItem = true;
                        break;
                    }
                }

                if (!pickedItem) {
                    for (BaseTask task : tasks) {
                        if (!task.isCompleted && player.position.dst(task.getBounds().x, task.getBounds().y) < 60f) {
                            activeTask = task;
                            task.interact(player);
                            break;
                        }
                    }
                }
            }
        } else {
            player.updateIdle(delta);
        }

        ghost.update(player, delta);

        if (ghost.getPosition().dst(player.position) < 20f) {
            activeTask = null;
            ScreenManager.getInstance().setScreen(new GameOverScreen());
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        uiBatch.dispose();
        lightingSystem.dispose();
        gameHUD.dispose();
    }
}
