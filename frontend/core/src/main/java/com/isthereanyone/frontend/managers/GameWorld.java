package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapGroupLayer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Gate;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;
import com.isthereanyone.frontend.entities.items.ItemType;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.entities.tasks.TaskFactory;
import com.isthereanyone.frontend.observer.EventManager;

public class GameWorld {
    public TiledMap map;
    public Array<Rectangle> walls;
    public Array<RectangleMapObject> teleports;
    public Array<RectangleMapObject> hideSpots;

    public Player player;
    public Ghost ghost;
    public Array<BaseTask> tasks;
    public Array<RitualItem> itemsOnGround;
    public Gate gate;

    public GameWorld() {
        try {
            map = new TmxMapLoader().load("Tilemap.tmx");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load map.");
            map = new TiledMap();
        }

        walls = new Array<>();
        teleports = new Array<>();
        hideSpots = new Array<>();
        tasks = new Array<>();

        parseManualCollisionLayer();
        parseObjectLayerCollisions("Details");
        parseObjectLayerCollisions("Interior Details");
        parseObjectLayerCollisions("Collidables");
        parseTileLayerCollisions();

        parseInteractionLayer();
        parseEntitiesLayer();

        if (player == null) player = new Player(384, 384);
        if (ghost == null) ghost = new Ghost(1200, 1200);
        if (gate == null) gate = new Gate(1200, 800);

        EventManager.getInstance().addObserver(ghost);
        spawnItems();
        setupGhostPatrol();
    }

    private void parseEntitiesLayer() {
        MapLayer layer = null;
        MapLayer logicGroup = map.getLayers().get("Logic");
        if (logicGroup instanceof MapGroupLayer) {
            layer = ((MapGroupLayer) logicGroup).getLayers().get("Entities");
        }
        if (layer == null) layer = map.getLayers().get("Entities");

        if (layer == null) return;

        for (MapObject object : layer.getObjects()) {
            String type = null;
            if (object.getProperties().containsKey("Type")) {
                type = object.getProperties().get("Type", String.class);
            }

            float x = 0;
            float y = 0;
            TextureRegion textureFromTiled = null;

            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                x = rect.x; y = rect.y;
            } else if (object instanceof TiledMapTileMapObject) {
                TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
                x = tileObj.getX();
                y = tileObj.getY();

                if (tileObj.getTile() != null) {
                    textureFromTiled = tileObj.getTile().getTextureRegion();
                }
            } else {
                x = object.getProperties().get("x", Float.class);
                y = object.getProperties().get("y", Float.class);
            }

            if (type != null) {
                switch (type) {
                    case "PlayerStart":
                        player = new Player(x, y);
                        break;
                    case "GhostStart":
                        ghost = new Ghost(x, y);
                        break;

                    case "Gate":
                        gate = new Gate(x, y);
                        if (textureFromTiled != null) {
                            gate.setTexture(textureFromTiled);
                            System.out.println("DEBUG: Gate texture loaded from Tiled!");
                        }
                        break;

                    case "Task":
                        if (object.getProperties().containsKey("TaskType")) {
                            String taskType = object.getProperties().get("TaskType", String.class);
                            BaseTask newTask = TaskFactory.createTask(taskType, x, y);

                            if (textureFromTiled != null) {
                                newTask.setTexture(textureFromTiled);
                                newTask.getBounds().setWidth(textureFromTiled.getRegionWidth());
                                newTask.getBounds().setHeight(textureFromTiled.getRegionHeight());
                            }

                            boolean hasCustomCollision = false;
                            if (!hasCustomCollision) {
                                walls.add(new Rectangle(x, y, newTask.getBounds().width, newTask.getBounds().height));
                            }
                            tasks.add(newTask);
                        }
                        break;
                }
            }
        }
    }

    private void parseInteractionLayer() {
        MapLayer interactionLayer = null;
        String[] layerNames = {"Interactions", "Interaction Details", "Teleports"};
        MapLayer logicGroup = map.getLayers().get("Logic");

        if (logicGroup instanceof MapGroupLayer) {
            for (String name : layerNames) {
                interactionLayer = ((MapGroupLayer) logicGroup).getLayers().get(name);
                if (interactionLayer != null) break;
            }
        }
        if (interactionLayer == null) {
            for (String name : layerNames) {
                interactionLayer = map.getLayers().get(name);
                if (interactionLayer != null) break;
            }
        }

        if (interactionLayer != null) {
            for (MapObject object : interactionLayer.getObjects()) {
                if (object.getProperties().containsKey("Type")) {
                    String type = object.getProperties().get("Type", String.class);

                    Rectangle rect = null;
                    if (object instanceof RectangleMapObject) {
                        rect = ((RectangleMapObject) object).getRectangle();
                    } else if (object instanceof TiledMapTileMapObject) {
                        TiledMapTileMapObject tileObj = (TiledMapTileMapObject) object;
                        rect = new Rectangle(tileObj.getX(), tileObj.getY(),
                            tileObj.getTextureRegion().getRegionWidth(),
                            tileObj.getTextureRegion().getRegionHeight());
                    }

                    if (rect != null) {
                        RectangleMapObject wrapper = new RectangleMapObject(rect.x, rect.y, rect.width, rect.height);
                        wrapper.getProperties().putAll(object.getProperties());

                        if ("Teleport".equals(type)) {
                            teleports.add(wrapper);
                        } else if ("HideSpot".equals(type)) {
                            hideSpots.add(wrapper);
                            System.out.println("DEBUG: HideSpot found: " + object.getName());
                        }
                    }
                }
            }
        }
    }

    private void parseObjectLayerCollisions(String layerName) {
        MapLayer objectLayer = map.getLayers().get(layerName);
        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                    TiledMapTile tile = tileObject.getTile();
                    if (tile != null && tile.getObjects().getCount() > 0) {
                        for (MapObject tileCollision : tile.getObjects()) {
                            if (tileCollision instanceof RectangleMapObject) {
                                Rectangle localRect = ((RectangleMapObject) tileCollision).getRectangle();
                                walls.add(new Rectangle(tileObject.getX() + localRect.x, tileObject.getY() + localRect.y, localRect.width, localRect.height));
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseTileLayerCollisions() {
        for (MapLayer layer : map.getLayers()) {
            boolean isWallLayer = layer.getName().equals("Collidables") || layer.getName().equals("Interior");
            if (isWallLayer && layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
                        if (cell != null && cell.getTile() != null) {
                            if (cell.getTile().getObjects().getCount() > 0) {
                                for (MapObject obj : cell.getTile().getObjects()) {
                                    if (obj instanceof RectangleMapObject) {
                                        Rectangle localRect = ((RectangleMapObject) obj).getRectangle();
                                        walls.add(new Rectangle((x*32)+localRect.x, (y*32)+localRect.y, localRect.width, localRect.height));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseManualCollisionLayer() {
        MapLayer logicGroup = map.getLayers().get("Logic");
        if (logicGroup instanceof MapGroupLayer) {
            MapLayer collisionLayer = ((MapGroupLayer) logicGroup).getLayers().get("Collision");
            if (collisionLayer != null) {
                for (MapObject object : collisionLayer.getObjects()) {
                    if (object instanceof RectangleMapObject) walls.add(((RectangleMapObject) object).getRectangle());
                }
            }
        }
    }

    private void spawnItems() {
        itemsOnGround = new Array<>();
        Array<ItemType> itemPool = new Array<>();
        for (ItemType type : ItemType.values()) itemPool.add(type);
        itemPool.shuffle();

        float minVal = 350f;
        float maxVal = 4800f;
        float altarX = (gate != null) ? gate.getBounds().x : 1000;
        float altarY = (gate != null) ? gate.getBounds().y : 1000;

        for (ItemType type : itemPool) {
            boolean valid = false; int attempts = 0; float x = 0, y = 0;
            while (!valid && attempts < 100) {
                attempts++;
                x = MathUtils.random(minVal, maxVal);
                y = MathUtils.random(minVal, maxVal);
                if (Vector2.dst(x, y, altarX, altarY) < 250f) continue;

                Rectangle candidateRect = new Rectangle(x, y, 24, 24);
                boolean collisionFound = false;
                for (RitualItem existing : itemsOnGround) {
                    if (candidateRect.overlaps(existing.getBounds())) { collisionFound = true; break; }
                }
                if (collisionFound) continue;
                for (Rectangle wall : walls) {
                    if (candidateRect.overlaps(wall)) { collisionFound = true; break; }
                }
                if (collisionFound) continue;
                for (RectangleMapObject tele : teleports) {
                    if (candidateRect.overlaps(tele.getRectangle())) { collisionFound = true; break; }
                }
                if (collisionFound) continue;

                valid = true;
            }
            if (valid) itemsOnGround.add(new RitualItem(type, x, y));
        }
    }

    private void setupGhostPatrol() {
        Array<Vector2> ghostWaypoints = new Array<>();
        for (BaseTask task : tasks) ghostWaypoints.add(new Vector2(task.getBounds().x, task.getBounds().y));
        if (gate != null) ghostWaypoints.add(new Vector2(gate.getBounds().x, gate.getBounds().y));
        ghost.setPatrolPoints(ghostWaypoints);
    }

    public void dispose() { if (map != null) map.dispose(); }
}
