package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
    public Player player;
    public Ghost ghost;
    public Array<BaseTask> tasks;
    public Array<RitualItem> itemsOnGround;
    public Gate gate;

    public GameWorld() {
        player = new Player(100, 100);
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
                x = MathUtils.random(50, 950); y = MathUtils.random(50, 950);
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

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(-1000, -1000, 4000, 4000);

        for (BaseTask task : tasks) task.render(shapeRenderer);
        for (RitualItem item : itemsOnGround) item.render(batch, shapeRenderer);
        gate.render(shapeRenderer);

        shapeRenderer.end();

        batch.begin();
        player.render(batch);
        ghost.render(batch);
        batch.end();
    }

    public void dispose() {
    }
}
