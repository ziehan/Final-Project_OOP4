package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.isthereanyone.frontend.managers.ScreenManager;

public class MainMenuScreen extends BaseScreen{
    private ShapeRenderer shapeRenderer;

    public MainMenuScreen(){
        super();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0.2f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(220, 160, 200, 50);
        shapeRenderer.end();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            System.out.println("Moving to playing state...");
            ScreenManager.getInstance().setScreen(new PlayScreen());
        }
    }

    @Override
    public void dispose(){
        shapeRenderer.dispose();
    }
}
