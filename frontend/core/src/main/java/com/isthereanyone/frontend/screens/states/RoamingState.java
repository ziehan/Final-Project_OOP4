package com.isthereanyone.frontend.screens.states;

import com.badlogic.gdx.Gdx;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.screens.GameOverScreen;
import com.isthereanyone.frontend.screens.PlayScreen;

public class RoamingState implements PlayState {
    private final PlayScreen screen;
    private float deathTimer = 0f;

    public RoamingState(PlayScreen screen) {
        this.screen = screen;
    }

    @Override
    public void update(float delta) {
        Player player = screen.getWorld().player;

        if (player.isDead) {
            player.update(delta);
            screen.getWorld().ghost.update(player, delta);
            deathTimer += delta;

            screen.camera.position.set(player.position.x + 16, player.position.y + 16, 0);
            screen.camera.update();

            if (deathTimer > 2.0f) {
                ScreenManager.getInstance().setScreen(new GameOverScreen());
            }
            return;
        }

        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        screen.viewport.update(screenW, screenH, false);
        screen.viewport.setScreenBounds(0, 0, screenW, screenH);
        screen.viewport.apply();

        screen.camera.zoom = 1.0f;
        screen.camera.position.set(player.position.x + 16, player.position.y + 16, 0);
        screen.camera.update();

        player.update(delta);
        screen.inputHandler.handleInput(player, delta);

        if (player.position.x < 0) player.position.x = 0;
        if (player.position.x > 1000) player.position.x = 1000;
        if (player.position.y < 0) player.position.y = 0;
        if (player.position.y > 1000) player.position.y = 1000;

        screen.getWorld().ghost.update(player, delta);

        float dist = screen.getWorld().ghost.getPosition().dst(player.position);

        if (dist < 30f && !screen.getWorld().ghost.isCoolingDown()) {
            screen.getWorld().ghost.triggerAttackCooldown();
        }

        if (screen.getWorld().ghost.shouldDealDamage()) {
            if (dist < 50f) {
                player.takeDamage();
            }
            screen.getWorld().ghost.confirmDamageDealt();
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
