package com.isthereanyone.frontend.screens.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.screens.GameOverScreen;
import com.isthereanyone.frontend.screens.PlayScreen;

import java.util.Comparator;

public class RoamingState implements PlayState {
    private final PlayScreen screen;
    private float deathTimer = 0f;
    private boolean debugMode = false;

    private final int[] floorLayers = { 0, 1 };

    private final int[] wallLayers = { 3 };

    private final int[] foregroundLayers = { 4 };

    private Array<DepthObject> renderQueue;
    private DepthComparator depthComparator;

    public RoamingState(PlayScreen screen) {
        this.screen = screen;
        this.renderQueue = new Array<>();
        this.depthComparator = new DepthComparator();
    }

    @Override
    public void update(float delta) {
        Player player = screen.getWorld().player;

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            debugMode = !debugMode;
        }

        if (player.isDead) {
            handleDeath(delta, player);
            return;
        }

        updateCamera(player);

        handleInputAndCollision(delta, player);

        handleGhostAndAnimation(delta, player);
    }

    @Override
    public void render() {
        screen.mapRenderer.render(floorLayers);
        screen.mapRenderer.render(wallLayers);

        screen.batch.setProjectionMatrix(screen.camera.combined);
        screen.batch.begin();

        renderQueue.clear();

        Player player = screen.getWorld().player;
        renderQueue.add(new DepthObject(player.position.y, () -> player.render(screen.batch)));

        renderQueue.add(new DepthObject(screen.getWorld().ghost.getPosition().y, () -> screen.getWorld().ghost.render(screen.batch)));

        MapLayer detailsLayer = screen.getWorld().map.getLayers().get("Details");
        if (detailsLayer != null) {
            for (MapObject object : detailsLayer.getObjects()) {
                if (object instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
                    if (tileObj.getTile() != null) {
                        TextureRegion region = tileObj.getTile().getTextureRegion();

                        float realX = tileObj.getX();
                        float realY = tileObj.getY();

                        float offset = 15.0f;
                        float sortY = realY + offset;

                        renderQueue.add(new DepthObject(sortY, () -> {
                            screen.batch.draw(region, realX, realY);
                        }));
                    }
                }
            }
        }

        renderQueue.sort(depthComparator);

        for (DepthObject obj : renderQueue) {
            obj.drawRunnable.run();
        }

        screen.batch.end();

        screen.mapRenderer.render(foregroundLayers);

        if (debugMode) {
            renderDebugCollision();
        }

        renderUIAndLighting();
    }

    private static class DepthObject {
        float y;
        Runnable drawRunnable;

        DepthObject(float y, Runnable run) {
            this.y = y;
            this.drawRunnable = run;
        }
    }

    private static class DepthComparator implements Comparator<DepthObject> {
        @Override
        public int compare(DepthObject o1, DepthObject o2) {
            return Float.compare(o2.y, o1.y);
        }
    }

    private void handleDeath(float delta, Player player) {
        player.update(delta);
        screen.getWorld().ghost.update(player, delta);
        deathTimer += delta;
        screen.camera.position.set(player.position.x + 16, player.position.y + 16, 0);
        screen.camera.update();
        if (deathTimer > 2.0f) {
            ScreenManager.getInstance().setScreen(new GameOverScreen());
        }
    }

    private void updateCamera(Player player) {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        screen.viewport.update(screenW, screenH, false);
        screen.viewport.setScreenBounds(0, 0, screenW, screenH);
        screen.viewport.apply();

        screen.camera.zoom = 1.0f;
        screen.camera.position.set(player.position.x + 16, player.position.y + 16, 0);
        screen.camera.update();
        screen.mapRenderer.setView(screen.camera);
    }

    private void handleInputAndCollision(float delta, Player player) {
        float oldX = player.position.x;
        float oldY = player.position.y;

        player.update(delta);
        screen.inputHandler.handleInput(player, delta);

        float mapSize = 2400f;
        if (player.position.x < 0) player.position.x = 0;
        if (player.position.x > mapSize - 32) player.position.x = mapSize - 32;
        if (player.position.y < 0) player.position.y = 0;
        if (player.position.y > mapSize - 32) player.position.y = mapSize - 32;

        // Collision Check
        Rectangle playerRect = new Rectangle(player.position.x + 10, player.position.y, 14, 10);
        for (Rectangle wall : screen.getWorld().walls) {
            if (playerRect.overlaps(wall)) {
                player.position.set(oldX, oldY);
                break;
            }
        }
    }

    private void handleGhostAndAnimation(float delta, Player player) {
        screen.getWorld().ghost.update(player, delta);
        float dist = screen.getWorld().ghost.getPosition().dst(player.position);
        if (dist < 30f && !screen.getWorld().ghost.isCoolingDown()) {
            screen.getWorld().ghost.triggerAttackCooldown();
        }
        if (screen.getWorld().ghost.shouldDealDamage()) {
            if (dist < 50f) player.takeDamage();
            screen.getWorld().ghost.confirmDamageDealt();
        }
        AnimatedTiledMapTile.updateAnimationBaseTime();
    }

    private void renderDebugCollision() {
        screen.shapeRenderer.setProjectionMatrix(screen.camera.combined);
        screen.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        screen.shapeRenderer.setColor(1, 0, 0, 1);
        for (Rectangle wall : screen.getWorld().walls) {
            screen.shapeRenderer.rect(wall.x, wall.y, wall.width, wall.height);
        }
        screen.shapeRenderer.end();
    }

    private void renderUIAndLighting() {
        int screenW = Gdx.graphics.getWidth();
        int screenH = Gdx.graphics.getHeight();
        screen.uiViewport.update(screenW, screenH, true);
        screen.uiViewport.setScreenBounds(0, 0, screenW, screenH);

        screen.shapeRenderer.setProjectionMatrix(screen.camera.combined);
        screen.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BaseTask task : screen.getWorld().tasks) task.render(screen.shapeRenderer);
        for (RitualItem item : screen.getWorld().itemsOnGround) item.render(screen.batch, screen.shapeRenderer);
        screen.getWorld().gate.render(screen.shapeRenderer);
        screen.shapeRenderer.end();

        screen.lightingSystem.renderLightMap(screen.batch, screen.getWorld().player, screen.camera);
        screen.lightingSystem.renderDarkness(screen.uiBatch, screen.uiViewport);
        screen.renderHUD();
    }
}
