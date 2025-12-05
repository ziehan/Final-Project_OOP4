package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
<<<<<<< Updated upstream
=======
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.observer.EventManager;
>>>>>>> Stashed changes

public class PlayScreen extends BaseScreen {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;


    private Player player;
    private Ghost ghost;
    private BaseTask task1;
    private InputHandler inputHandler;
<<<<<<< Updated upstream
=======
    private Array<BaseTask> tasks;
    private FrameBuffer lightBuffer;
    private TextureRegion lightBufferRegion;
    private Texture lightTexture;
    private SpriteBatch uiBatch;
    private Viewport uiViewport;
>>>>>>> Stashed changes

    public PlayScreen() {
        super();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        inputHandler = new InputHandler();


<<<<<<< Updated upstream
        player = new Player(100, 100);
        ghost = new Ghost(300, 300);


        task1 = TaskFactory.createTask("SIMPLE", 150, 150);
=======
        tasks = new Array<>();
        tasks.add(TaskFactory.createTask("WIRE", 200, 200));
        tasks.add(TaskFactory.createTask("RITUAL", 500, 300));

        OrthographicCamera uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, uiCamera);
        uiBatch = new SpriteBatch();

        lightTexture = createGradientCircle(300);

        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int)GameConfig.VIEWPORT_WIDTH, (int)GameConfig.VIEWPORT_HEIGHT, false);
        lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture());
        lightBufferRegion.flip(false, true);
    }

    private Texture createGradientCircle(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        int radius = size / 2;
        int centerX = size / 2;
        int centerY = size / 2;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double dist = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));

                if (dist < radius) {
                    float alpha = 1f - (float) (dist / radius);

                    pixmap.setColor(1f, 1f, 1f, alpha);
                    pixmap.drawPixel(x, y);
                }
            }
        }

        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height, true);
>>>>>>> Stashed changes
    }

    @Override
    public void render(float delta) {
        lightBuffer.begin();

        Gdx.gl.glClearColor(0, 0, 0, 0.95f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.draw(lightTexture,
            player.position.x - lightTexture.getWidth()/2 + 16,
            player.position.y - lightTexture.getHeight()/2 + 16);

        batch.end();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        lightBuffer.end();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

<<<<<<< Updated upstream

        camera.position.set(player.position.x, player.position.y, 0);
=======
        camera.position.set(player.position.x + 16, player.position.y + 16, 0);
>>>>>>> Stashed changes
        camera.update();


        inputHandler.handleInput(player, delta);
<<<<<<< Updated upstream


        ghost.update(player, delta);

=======
        if (player.position.x < 0) player.position.x = 0;
        if (player.position.x > 800 - 32) player.position.x = 800 - 32;
        if (player.position.y < 0) player.position.y = 0;
        if (player.position.y > 600 - 32) player.position.y = 600 - 32;
        ghost.update(player, delta);

        if (ghost.getPosition().dst(player.position) < 20f) {
            ScreenManager.getInstance().setScreen(new GameOverScreen());
            return;
        }
>>>>>>> Stashed changes

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            task1.interact(player);
        }


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
        uiBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        uiBatch.begin();
        uiBatch.draw(lightBufferRegion, 0, 0, GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT);
        uiBatch.end();

        uiViewport.apply();
        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(10, GameConfig.VIEWPORT_HEIGHT - 30, 104, 14);
        if (player.currentStamina > 30) {
            shapeRenderer.setColor(0f, 0.8f, 0f, 1f);
        } else {
            shapeRenderer.setColor(0.8f, 0f, 0f, 1f);
        }
        float barWidth = (player.currentStamina / player.maxStamina) * 100f;
        shapeRenderer.rect(12, GameConfig.VIEWPORT_HEIGHT - 28, barWidth, 10);
        shapeRenderer.end();

        viewport.apply();
>>>>>>> Stashed changes
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        uiBatch.dispose();
        lightTexture.dispose();
        lightBuffer.dispose();
    }
}
