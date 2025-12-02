package com.isthereanyone.frontend.entities.ghost;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.strategies.GhostStrategy;
import com.isthereanyone.frontend.entities.ghost.strategies.PatrolStrategy;

public class Ghost {
    public Vector2 position;
    private GhostStrategy currentStrategy;

    public Ghost(float x, float y){
        this.position = new Vector2(x, y);
        this.currentStrategy = new PatrolStrategy();
    }

    public void setCurrentStrategy(GhostStrategy strategy){
        this.currentStrategy = strategy;
    }

    public void update(Player player, float delta){
        currentStrategy.executeBehavior(this, player, delta);
    }

    public void render(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(position.x, position.y, 32, 32);
    }
}
