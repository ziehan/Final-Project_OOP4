package com.isthereanyone.frontend.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;

public class BaseScreen implements Screen {
    public OrthographicCamera camera;
    public Viewport viewport;

    public BaseScreen(){
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.VIEWPORT_WIDTH, GameConfig.VIEWPORT_HEIGHT, camera);
        camera.position.set(GameConfig.VIEWPORT_WIDTH / 2, GameConfig.VIEWPORT_HEIGHT / 2, 0);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
