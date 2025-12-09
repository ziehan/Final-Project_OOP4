package com.isthereanyone.frontend.screens.states;

import com.badlogic.gdx.Gdx;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.screens.GameOverScreen;
import com.isthereanyone.frontend.screens.PlayScreen;

public class RoamingState implements PlayState {
    private final PlayScreen screen;

    public RoamingState(PlayScreen screen) {
        this.screen = screen;
    }

    @Override
    public void update(float delta) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        screen.viewport.update(screenW, screenH, false);
        screen.viewport.setScreenBounds(0, 0, screenW, screenH);
        screen.viewport.apply();

        screen.camera.zoom = 1.0f;
        screen.camera.position.set(screen.getWorld().player.position.x + 16, screen.getWorld().player.position.y + 16, 0);
        screen.camera.update();

        screen.inputHandler.handleInput(screen.getWorld().player, delta);

        if (screen.getWorld().player.position.x < 0) screen.getWorld().player.position.x = 0;
        if (screen.getWorld().player.position.x > 1000) screen.getWorld().player.position.x = 1000;
        if (screen.getWorld().player.position.y < 0) screen.getWorld().player.position.y = 0;
        if (screen.getWorld().player.position.y > 1000) screen.getWorld().player.position.y = 1000;

        screen.getWorld().ghost.update(screen.getWorld().player, delta);

        if (screen.getWorld().ghost.getPosition().dst(screen.getWorld().player.position) < 20f) {
            ScreenManager.getInstance().setScreen(new GameOverScreen());
        }
    }

    @Override
    public void render() {
        screen.renderWorld();

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        screen.uiViewport.update(screenW, screenH, true);
        screen.uiViewport.setScreenBounds(0, 0, screenW, screenH);

        screen.lightingSystem.renderDarkness(screen.uiBatch, screen.uiViewport);
        screen.renderHUD();
    }
}
