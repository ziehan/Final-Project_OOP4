package com.isthereanyone.frontend.screens.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.managers.AudioManager;
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

            if (player.isHidden) {
                player.isHidden = false;
                player.position.y -= 32f;
                System.out.println("Player came out from hiding.");
                return;
            }

            for (RectangleMapObject spot : screen.getWorld().hideSpots) {
                Rectangle r = spot.getRectangle();
                Vector2 spotCenter = new Vector2();
                r.getCenter(spotCenter);

                if (player.position.dst(spotCenter) < 40f) {
                    player.isHidden = true;
                    player.position.set(spotCenter.x - 8, spotCenter.y);
                    player.currentState = Player.State.IDLE;
                    System.out.println("Player is hiding in: " + spot.getName());
                    return;
                }
            }

            for (RitualItem item : screen.getWorld().itemsOnGround) {
                if (player.position.dst(item.getPosition()) < 50f) {
                    System.out.println("Interacted with Item: " + item.getType());
                    // item.isCollected = true;
                    return;
                }
            }

            for (BaseTask task : screen.getWorld().tasks) {
                if (player.position.dst(task.getPosition()) < 30f) {
                    System.out.println("Interacted with Task: " + task.getType());
                    task.interact(player);
                    return;
                }
            }

            screen.getWorld().gate.update(screen.getWorld());

            if (screen.getWorld().player.position.dst(screen.getWorld().gate.getBounds().x, screen.getWorld().gate.getBounds().y) < 30f) {
                screen.getWorld().gate.interact();
                return;
            }
        }
    }

    private void handleTeleport() {
        Player player = screen.getWorld().player;
        Ghost ghost = screen.getWorld().ghost;
        Rectangle playerRect = player.getBoundingRectangle();

        for (RectangleMapObject teleportObj : screen.getWorld().teleports) {
            if (playerRect.overlaps(teleportObj.getRectangle())) {
                if (teleportObj.getProperties().containsKey("destX")) {
                    float destX = teleportObj.getProperties().get("destX", Float.class);
                    float rawDestY = teleportObj.getProperties().get("destY", Float.class);

                    int mapHeightTiles = screen.getWorld().map.getProperties().get("height", Integer.class);
                    int tileHeight = screen.getWorld().map.getProperties().get("tileheight", Integer.class);
                    float finalDestY = (mapHeightTiles * tileHeight) - rawDestY;

                    float distToGhost = player.position.dst(ghost.getPosition());
                    float doorX = player.position.x;
                    float doorY = player.position.y;

                    player.position.set(destX, finalDestY);
                    screen.camera.position.set(destX + 16, finalDestY + 16, 0);
                    screen.camera.update();

                    if (distToGhost < 300f) {
                        ghost.pursuePlayerTo(destX, finalDestY);
                    } else {
                        ghost.investigatePosition(doorX, doorY);
                    }
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

        renderQueue.add(new DepthObject(
            screen.getWorld().gate.getBounds().y,
            () -> screen.getWorld().gate.render(screen.batch)
        ));

        for (RitualItem item : screen.getWorld().itemsOnGround) {
            renderQueue.add(new DepthObject(item.getPosition().y, () -> item.render(screen.batch, null)));
        }

        for (BaseTask task : screen.getWorld().tasks) {
            float sortY = task.getPosition().y + 5.0f;
            renderQueue.add(new DepthObject(sortY, () -> task.render(screen.batch)));
        }

        processLayerForSorting("Details", 5.0f);
        processLayerForSorting("Interior Details", 5.0f);
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

        if (screen.getWorld().player.isHidden) {
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            screen.uiBatch.begin();
            screen.shapeRenderer.setProjectionMatrix(screen.uiViewport.getCamera().combined);
            screen.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            screen.shapeRenderer.setColor(0, 0, 0, 0.9f);
            screen.shapeRenderer.rect(0, 0, screenW, screenH);

            screen.shapeRenderer.end();
            screen.uiBatch.end();
        }

        screen.lightingSystem.renderDarkness(screen.uiBatch, screen.uiViewport);
        screen.renderHUD();
    }

    private void processLayerForSorting(String layerName, float offset) {
        MapLayer layer = screen.getWorld().map.getLayers().get(layerName);
        if (layer == null) return;

        for (MapObject object : layer.getObjects()) {
            if (object instanceof TiledMapTileMapObject) {
                TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
                if (tileObj.getTile() != null) {
                    TextureRegion region = tileObj.getTile().getTextureRegion();
                    final float drawX = tileObj.getX();
                    final float drawY = tileObj.getY();
                    final float sortY = tileObj.getY() + offset;

                    renderQueue.add(new DepthObject(sortY, () -> {
                        screen.batch.draw(region, drawX, drawY);
                    }));
                }
            }
        }

        if (layer instanceof TiledMapTileLayer) {
            TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
            int startX = (int) (screen.camera.position.x - screen.camera.viewportWidth/2) / 32;
            int endX = (int) (screen.camera.position.x + screen.camera.viewportWidth/2) / 32 + 2;
            int startY = (int) (screen.camera.position.y - screen.camera.viewportHeight/2) / 32;
            int endY = (int) (screen.camera.position.y + screen.camera.viewportHeight/2) / 32 + 2;

            for (int x = startX; x < endX; x++) {
                for (int y = startY; y < endY; y++) {
                    if (x < 0 || y < 0 || x >= tileLayer.getWidth() || y >= tileLayer.getHeight()) continue;
                    TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                    if (cell != null && cell.getTile() != null) {
                        TextureRegion region = cell.getTile().getTextureRegion();
                        final float worldX = x * 32;
                        final float worldY = y * 32;
                        final float sortY = worldY + offset;
                        renderQueue.add(new DepthObject(sortY, () -> {
                            screen.batch.draw(region, worldX, worldY);
                        }));
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
        // Stop all SFX when dead
        AudioManager.getInstance().stopAllSfx();

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
        float oldX = player.position.x;
        float oldY = player.position.y;
        player.update(delta);
        screen.inputHandler.handleInput(player, delta);

        float mapSize = screen.getWorld().map.getProperties().get("width", Integer.class) * 32f;
        if (player.position.x < 0) player.position.x = 0;
        if (player.position.x > mapSize - 32) player.position.x = mapSize - 32;
        if (player.position.y < 0) player.position.y = 0;
        if (player.position.y > mapSize - 32) player.position.y = mapSize - 32;

        Rectangle playerRect = player.getBoundingRectangle();

        for (Rectangle wall : screen.getWorld().walls) {
            if (playerRect.overlaps(wall)) {
                player.position.set(oldX, oldY);
                break;
            }
        }

        com.isthereanyone.frontend.entities.Gate gate = screen.getWorld().gate;
        if (gate != null && gate.isLocked()) {
            if (playerRect.overlaps(gate.getBounds())) {
                player.position.set(oldX, oldY);
            }
        }
    }

    private void handleGhostAndAnimation(float delta, Player player) {
        Ghost ghost = screen.getWorld().ghost;
        ghost.update(player, delta);

        // Handle player running SFX
        if (player.isRunning() && player.isMoving()) {
            AudioManager.getInstance().playRunningSound();
        } else {
            AudioManager.getInstance().stopRunningSound();
        }

        // Handle ghost wings SFX
        if (ghost.isMoving()) {
            AudioManager.getInstance().playWingsSound();
        } else {
            AudioManager.getInstance().stopWingsSound();
        }

        float dist = ghost.getPosition().dst(player.position);
        if (dist < 30f && !ghost.isCoolingDown()) ghost.triggerAttackCooldown();
        if (ghost.shouldDealDamage()) {
            if (dist < 50f) {
                player.takeDamage();
                // Play stab/hit sound when player takes damage
                AudioManager.getInstance().playStabSound();
            }
            ghost.confirmDamageDealt();
        }
        AnimatedTiledMapTile.updateAnimationBaseTime();
    }

    private void renderDebugCollision() {
        screen.shapeRenderer.setProjectionMatrix(screen.camera.combined);
        screen.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Player player = screen.getWorld().player;
        Rectangle pRect = player.getBoundingRectangle();
        screen.shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.RED);
        screen.shapeRenderer.rect(pRect.x, pRect.y, pRect.width, pRect.height);
        com.isthereanyone.frontend.entities.ghost.Ghost ghost = screen.getWorld().ghost;
        screen.shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.YELLOW);
        screen.shapeRenderer.rect(ghost.getPosition().x, ghost.getPosition().y, 32, 32);
        screen.shapeRenderer.setColor(0, 0, 1, 1);
        for (Rectangle wall : screen.getWorld().walls) {
            screen.shapeRenderer.rect(wall.x, wall.y, wall.width, wall.height);
        }
        screen.shapeRenderer.setColor(0, 1, 1, 1);
        for (RectangleMapObject tp : screen.getWorld().teleports) {
            Rectangle r = tp.getRectangle();
            screen.shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }
        screen.shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.PURPLE);
        for (RectangleMapObject hs : screen.getWorld().hideSpots) {
            Rectangle r = hs.getRectangle();
            screen.shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }
        screen.shapeRenderer.setColor(com.badlogic.gdx.graphics.Color.GREEN);
        Rectangle gRect = screen.getWorld().gate.getBounds();
        screen.shapeRenderer.rect(gRect.x, gRect.y, gRect.width, gRect.height);

        screen.shapeRenderer.end();
    }
}
