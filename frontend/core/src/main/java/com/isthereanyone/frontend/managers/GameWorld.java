package com.isthereanyone.frontend.managers;

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

        parseManualCollisionLayer();

        parseTileObjectCollisions();

        parseTileLayerCollisions();

        player = new Player(400, 400);
        ghost = new Ghost(900, 800);
        EventManager.getInstance().addObserver(ghost);

        tasks = new Array<>();
        tasks.add(TaskFactory.createTask("STONE", 200, 200));
        tasks.add(TaskFactory.createTask("GENERATOR", 500, 300));
        tasks.add(TaskFactory.createTask("RITUAL", 800, 500));

        gate = new Gate(900, 500);

        spawnItems();

        Array<Vector2> ghostWaypoints = new Array<>();
        for (BaseTask task : tasks) {
            ghostWaypoints.add(new Vector2(task.getBounds().x, task.getBounds().y));
        }
        ghostWaypoints.add(new Vector2(gate.getBounds().x, gate.getBounds().y));
        ghost.setPatrolPoints(ghostWaypoints);
    }

    private void parseManualCollisionLayer() {
        MapLayer logicGroup = map.getLayers().get("Logic");
        if (logicGroup instanceof MapGroupLayer) {
            MapGroupLayer group = (MapGroupLayer) logicGroup;
            MapLayer collisionLayer = group.getLayers().get("Collision");
            if (collisionLayer != null) {
                for (MapObject object : collisionLayer.getObjects()) {
                    if (object instanceof RectangleMapObject) {
                        walls.add(((RectangleMapObject) object).getRectangle());
                    }
                }
            }
        }
    }

    private void parseTileObjectCollisions() {
        MapLayer objectLayer = map.getLayers().get("Details");

        if (objectLayer != null) {
            for (MapObject object : objectLayer.getObjects()) {
                if (object instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
                    TiledMapTile tile = tileObject.getTile();

                    if (tile != null && tile.getObjects().getCount() > 0) {
                        for (MapObject tileCollision : tile.getObjects()) {
                            if (tileCollision instanceof RectangleMapObject) {
                                Rectangle localRect = ((RectangleMapObject) tileCollision).getRectangle();

                                float worldX = tileObject.getX() + localRect.x;
                                float worldY = tileObject.getY() + localRect.y;

                                walls.add(new Rectangle(worldX, worldY, localRect.width, localRect.height));
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseTileLayerCollisions() {
        for (MapLayer layer : map.getLayers()) {
            if (layer.getName().equals("Collidables") && layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

                for (int x = 0; x < tileLayer.getWidth(); x++) {
                    for (int y = 0; y < tileLayer.getHeight(); y++) {
                        TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);

                        if (cell != null && cell.getTile() != null) {
                            TiledMapTile tile = cell.getTile();

                            if (tile.getObjects().getCount() > 0) {
                                for (MapObject obj : tile.getObjects()) {
                                    if (obj instanceof RectangleMapObject) {
                                        Rectangle localRect = ((RectangleMapObject) obj).getRectangle();

                                        float worldX = (x * 32) + localRect.x;
                                        float worldY = (y * 32) + localRect.y;

                                        walls.add(new Rectangle(worldX, worldY, localRect.width, localRect.height));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void spawnItems() {
        itemsOnGround = new Array<>();
        Array<ItemType> itemPool = new Array<>();
        for (ItemType type : ItemType.values()) itemPool.add(type);
        itemPool.shuffle();

        float altarX = 800; float altarY = 500;

        for (ItemType type : itemPool) {
            boolean valid = false; int attempts = 0; float x = 0, y = 0;
            while (!valid && attempts < 50) {
                attempts++;
                x = MathUtils.random(100, 2300);
                y = MathUtils.random(100, 2300);
                if (Vector2.dst(x, y, altarX, altarY) < 250f) continue;
                boolean tooClose = false;
                for (RitualItem existing : itemsOnGround) {
                    if (Vector2.dst(x, y, existing.getBounds().x, existing.getBounds().y) < 150f) {
                        tooClose = true; break;
                    }
                }
                if (!tooClose) valid = true;
            }
            itemsOnGround.add(new RitualItem(type, x, y));
        }
    }

    public void dispose() {
        if (map != null) map.dispose();
    }
}
