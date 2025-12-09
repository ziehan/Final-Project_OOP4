package com.isthereanyone.frontend.screens.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.screens.GameOverScreen;
import com.isthereanyone.frontend.screens.PlayScreen;

public class TaskState implements PlayState {
    private final PlayScreen screen;
    private final BaseTask activeTask;

    public TaskState(PlayScreen screen, BaseTask activeTask) {
        this.screen = screen;
        this.activeTask = activeTask;
    }

    @Override
    public void update(float delta) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int halfW = screenW / 2;

        screen.viewport.update(halfW, screenH, false);
        screen.viewport.setScreenBounds(0, 0, halfW, screenH);
        screen.viewport.apply();

        screen.camera.zoom = 0.5f;
        screen.camera.position.set(screen.getWorld().player.position.x + 16, screen.getWorld().player.position.y + 16, 0);
        screen.camera.update();

        screen.getWorld().player.updateIdle(delta);
        screen.getWorld().ghost.update(screen.getWorld().player, delta);

        if (screen.getWorld().ghost.getPosition().dst(screen.getWorld().player.position) < 20f) {
            ScreenManager.getInstance().setScreen(new GameOverScreen());
        }

        int margin = 20;
        int maxDim = Math.min(halfW, screenH) - (2 * margin);

        int puzzleX = halfW + (halfW - maxDim) / 2;
        int puzzleY = (screenH - maxDim) / 2;

        screen.puzzleViewport.update(maxDim, maxDim, true);

        screen.puzzleViewport.setScreenBounds(puzzleX, puzzleY, maxDim, maxDim);
        screen.puzzleViewport.apply();

        boolean shouldClose = activeTask.updateMinigame(delta, screen.puzzleViewport, screen.getWorld().player);

        if (shouldClose) {
            screen.setState(new RoamingState(screen));
        }
    }

    @Override
    public void render() {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        int halfW = screenW / 2;

        screen.viewport.apply();
        screen.renderWorld();

        screen.uiViewport.update(halfW, screenH, true);
        screen.uiViewport.setScreenBounds(0, 0, halfW, screenH);
        screen.uiViewport.apply();
        screen.lightingSystem.renderDarkness(screen.uiBatch, screen.uiViewport);

        screen.puzzleViewport.apply();

        screen.shapeRenderer.setProjectionMatrix(screen.puzzleViewport.getCamera().combined);
        screen.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        screen.shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        float worldW = screen.puzzleViewport.getWorldWidth();
        float worldH = screen.puzzleViewport.getWorldHeight();
        float camX = screen.puzzleViewport.getCamera().position.x;
        float camY = screen.puzzleViewport.getCamera().position.y;

        screen.shapeRenderer.rect(camX - worldW/2, camY - worldH/2, worldW, worldH);
        screen.shapeRenderer.end();

        screen.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        screen.shapeRenderer.setColor(Color.GRAY);
        screen.shapeRenderer.rect(camX - worldW/2, camY - worldH/2, worldW, worldH);
        screen.shapeRenderer.end();

        activeTask.renderMinigame(screen.batch, screen.shapeRenderer, screen.puzzleViewport);
    }
}
