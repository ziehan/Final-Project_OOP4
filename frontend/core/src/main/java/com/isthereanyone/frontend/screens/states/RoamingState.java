package com.isthereanyone.frontend.screens.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.managers.GameWorld;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.screens.GameOverScreen;
import com.isthereanyone.frontend.screens.PlayScreen;
import java.util.Comparator;

public class RoamingState implements PlayState {
    private final PlayScreen screen;
    private float deathTimer = 0f;
    private boolean debugMode = false;

    private int[] floorLayers;
    private int[] foregroundLayers;

    private Array<DepthObject> renderQueue;
    private DepthComparator depthComparator;

    public RoamingState(PlayScreen screen) {
        this.screen = screen;
        this.renderQueue = new Array<>();
        this.depthComparator = new DepthComparator();
        detectLayers(screen.getWorld());
    }

    private void detectLayers(GameWorld world) {
        Array<Integer> bgLayerList = new Array<>();
        String[] possibleBgLayers = {"Floor", "Flower", "Grass", "Carpet"};
        for (String name : possibleBgLayers) {
            int idx = world.map.getLayers().getIndex(name);
            if (idx != -1) bgLayerList.add(idx);
        }
        floorLayers = new int[bgLayerList.size];
        for(int i=0; i<bgLayerList.size; i++) floorLayers[i] = bgLayerList.get(i);

        int foreIndex = world.map.getLayers().getIndex("Foreground");
        if (foreIndex != -1) foregroundLayers = new int[] { foreIndex };
        else foregroundLayers = new int[] {};
    }

    @Override
    public void update(float delta) {
        Player player = screen.getWorld().player;
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) debugMode = !debugMode;
        if (player.isDead) { handleDeath(delta, player); return; }

        handleInputAndCollision(delta, player);
        handleTeleport();
        handleInteraction();

        updateCamera(player);
        handleGhostAndAnimation(delta, player);
    }

    private void handleInteraction() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Player player = screen.getWorld().player;
            Rectangle playerRect = player.getBoundingRectangle();

            for (RitualItem item : screen.getWorld().itemsOnGround) {
                if (player.position.dst(item.getPosition()) < 50f) {
                    System.out.println("Interacted with Item: " + item.getType());
                    // item.isCollected = true; // Logic ambil item nanti
                    return;
                }
            }

            for (BaseTask task : screen.getWorld().tasks) {
                if (player.position.dst(task.getPosition()) < 60f) {
                    System.out.println("Interacted with Task: " + task.getType());
                    task.interact(player);
                    return;
                }
            }
        }
    }

    private void handleTeleport() {
        Player player = screen.getWorld().player;
        Rectangle playerRect = player.getBoundingRectangle();
        for (RectangleMapObject teleportObj : screen.getWorld().teleports) {
            if (playerRect.overlaps(teleportObj.getRectangle())) {
                if (teleportObj.getProperties().containsKey("destX")) {
                    float destX = teleportObj.getProperties().get("destX", Float.class);
                    float destY = teleportObj.getProperties().get("destY", Float.class);
                    player.position.set(destX, destY);
                    screen.camera.position.set(destX + 16, destY + 16, 0);
                    screen.camera.update();
                    break;
                }
            }
        }
    }

    @Override
    public void render() {
        if (floorLayers.length > 0) screen.mapRenderer.render(floorLayers);

        screen.batch.setProjectionMatrix(screen.camera.combined);
        screen.batch.begin();
        renderQueue.clear();

        Player player = screen.getWorld().player;

        renderQueue.add(new DepthObject(player.position.y, () -> player.render(screen.batch)));
        renderQueue.add(new DepthObject(screen.getWorld().ghost.getPosition().y, () -> screen.getWorld().ghost.render(screen.batch)));

        for (RitualItem item : screen.getWorld().itemsOnGround) {
            renderQueue.add(new DepthObject(item.getPosition().y, () -> item.render(screen.batch, null)));
        }
        for (BaseTask task : screen.getWorld().tasks) {
            renderQueue.add(new DepthObject(task.getPosition().y, () -> task.render(screen.batch)));
        }

        processLayerForSorting("Details", 15.0f);
        processLayerForSorting("Interior Details", 15.0f);
        processLayerForSorting("Interaction Details", 0.0f);
        processLayerForSorting("Collidables", 0.0f);
        processLayerForSorting("Interior", 0.0f);

        renderQueue.sort(depthComparator);
        for (DepthObject obj : renderQueue) obj.drawRunnable.run();

        screen.batch.end();

        if (foregroundLayers.length > 0) screen.mapRenderer.render(foregroundLayers);

        if (debugMode) renderDebugCollision();
        renderUIAndLighting();
    }

    private void renderUIAndLighting() {
        int screenW = Gdx.graphics.getWidth(); int screenH = Gdx.graphics.getHeight();
        screen.uiViewport.update(screenW, screenH, true);
        screen.uiViewport.setScreenBounds(0, 0, screenW, screenH);

        screen.lightingSystem.renderLightMap(screen.batch, screen.getWorld().player, screen.camera);
        screen.lightingSystem.renderDarkness(screen.uiBatch, screen.uiViewport);
        screen.renderHUD();
    }

    // --- HELPER METHOD DENGAN FIX ERROR LAMBDA ---
    private void processLayerForSorting(String layerName, float offset) {
        MapLayer layer = screen.getWorld().map.getLayers().get(layerName);
        if (layer == null) return;

        // 1. OBJECTS
        for (MapObject object : layer.getObjects()) {
            if (object instanceof TiledMapTileMapObject) {
                TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
                if (tileObj.getTile() != null) {
                    TextureRegion region = tileObj.getTile().getTextureRegion();

                    // FIX: Capture variable into final
                    final float drawX = tileObj.getX();
                    final float drawY = tileObj.getY();
                    final float sortY = tileObj.getY() + offset;

                    renderQueue.add(new DepthObject(sortY, () -> {
                        screen.batch.draw(region, drawX, drawY);
                    }));
                }
            }
        }

        // 2. TILE LAYERS (YANG ERROR KEMARIN)
        if (layer instanceof com.badlogic.gdx.maps.tiled.TiledMapTileLayer) {
            com.badlogic.gdx.maps.tiled.TiledMapTileLayer tileLayer = (com.badlogic.gdx.maps.tiled.TiledMapTileLayer) layer;
            int startX = (int) (screen.camera.position.x - screen.camera.viewportWidth/2) / 32;
            int endX = (int) (screen.camera.position.x + screen.camera.viewportWidth/2) / 32 + 2;
            int startY = (int) (screen.camera.position.y - screen.camera.viewportHeight/2) / 32;
            int endY = (int) (screen.camera.position.y + screen.camera.viewportHeight/2) / 32 + 2;

            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    if (x < 0 || y < 0 || x >= tileLayer.getWidth() || y >= tileLayer.getHeight()) continue;
                    com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        TextureRegion region = cell.getTile().getTextureRegion();

                        // --- FIX UTAMA DISINI ---
                        // Kita simpan hasil perkalian ke variabel final 'worldX' & 'worldY'
                        final float worldX = x * 32;
                        final float worldY = y * 32;
                        final float sortY = worldY + offset;

                        // Lambda sekarang pakai variabel final ini, bukan x/y loop
                        renderQueue.add(new DepthObject(sortY, () -> {
                            screen.batch.draw(region, worldX, worldY);
                        }));
                        // ------------------------
                    }
                }
            }
        }
    }

    private static class DepthObject {
        float y; Runnable drawRunnable;
        DepthObject(float y, Runnable run) { this.y = y; this.drawRunnable = run; }
    }
    private static class DepthComparator implements Comparator<DepthObject> {
        @Override
        public int compare(DepthObject o1, DepthObject o2) { return Float.compare(o2.y, o1.y); }
    }

    private void handleDeath(float delta, Player player) {
        player.update(delta);
        screen.getWorld().ghost.update(player, delta);
        deathTimer += delta;
        screen.camera.position.set(player.position.x + 16, player.position.y + 16, 0);
        screen.camera.update();
        if (deathTimer > 2.0f) ScreenManager.getInstance().setScreen(new GameOverScreen());
    }

    private void updateCamera(Player player) {
        int screenW = Gdx.graphics.getWidth(); int screenH = Gdx.graphics.getHeight();
        screen.viewport.update(screenW, screenH, false);
        screen.viewport.setScreenBounds(0, 0, screenW, screenH);
        screen.viewport.apply();
        screen.camera.zoom = 1.0f;
        screen.camera.position.set(player.position.x + 16, player.position.y + 16, 0);
        screen.camera.update();
        screen.mapRenderer.setView(screen.camera);
    }

    private void handleInputAndCollision(float delta, Player player) {
        float oldX = player.position.x; float oldY = player.position.y;
        player.update(delta);
        screen.inputHandler.handleInput(player, delta);
        float mapSize = screen.getWorld().map.getProperties().get("width", Integer.class) * 32f;
        if (player.position.x < 0) player.position.x = 0;
        if (player.position.x > mapSize - 32) player.position.x = mapSize - 32;
        if (player.position.y < 0) player.position.y = 0;
        if (player.position.y > mapSize - 32) player.position.y = mapSize - 32;
        Rectangle playerRect = new Rectangle(player.position.x + 10, player.position.y, 14, 10);
        for (Rectangle wall : screen.getWorld().walls) {
            if (playerRect.overlaps(wall)) { player.position.set(oldX, oldY); break; }
        }
    }

    private void handleGhostAndAnimation(float delta, Player player) {
        screen.getWorld().ghost.update(player, delta);
        float dist = screen.getWorld().ghost.getPosition().dst(player.position);
        if (dist < 30f && !screen.getWorld().ghost.isCoolingDown()) screen.getWorld().ghost.triggerAttackCooldown();
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
        for (Rectangle wall : screen.getWorld().walls) screen.shapeRenderer.rect(wall.x, wall.y, wall.width, wall.height);
        screen.shapeRenderer.setColor(0, 1, 1, 1);
        for (RectangleMapObject tp : screen.getWorld().teleports) {
            Rectangle r = tp.getRectangle();
            screen.shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }
        screen.shapeRenderer.end();
    }
}
