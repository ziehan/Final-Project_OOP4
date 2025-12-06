package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.config.GameConfig;
import com.isthereanyone.frontend.entities.Player;

public class LightingSystem {
    private FrameBuffer lightBuffer;
    private TextureRegion lightBufferRegion;
    private Texture lightTexture;

    private float ambientDarkness = 0.96f;

    public LightingSystem() {
        lightTexture = createGradientCircle(400);
        lightTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        resize((int)GameConfig.VIEWPORT_WIDTH, (int)GameConfig.VIEWPORT_HEIGHT);
    }

    public void resize(int width, int height) {
        if (lightBuffer != null) lightBuffer.dispose();

        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        lightBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        lightBufferRegion = new TextureRegion(lightBuffer.getColorBufferTexture());
        lightBufferRegion.flip(false, true);
    }

    public void renderLightMap(SpriteBatch batch, Player player, Camera gameCamera) {
        lightBuffer.begin();

        Gdx.gl.glClearColor(0f, 0f, 0f, ambientDarkness);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.draw(lightTexture,
            player.position.x - lightTexture.getWidth()/2 + 16,
            player.position.y - lightTexture.getHeight()/2 + 16);

        batch.end();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        lightBuffer.end();
    }

    public void renderDarkness(SpriteBatch uiBatch, Viewport uiViewport) {
        uiViewport.apply();
        uiBatch.setProjectionMatrix(uiViewport.getCamera().combined);
        uiBatch.begin();

        uiBatch.draw(lightBufferRegion,
            uiViewport.getCamera().position.x - uiViewport.getWorldWidth()/2,
            uiViewport.getCamera().position.y - uiViewport.getWorldHeight()/2,
            uiViewport.getWorldWidth(),
            uiViewport.getWorldHeight());

        uiBatch.end();
    }

    private Texture createGradientCircle(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        int radius = size / 2;
        int centerX = size / 2;
        int centerY = size / 2;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double dist = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (dist < radius) {
                    float alpha = 1f - (float) (dist / radius);
                    alpha = (float) Math.pow(alpha, 1.5);
                    pixmap.setColor(1f, 1f, 1f, alpha);
                    pixmap.drawPixel(x, y);
                }
            }
        }
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }

    public void dispose() {
        if (lightBuffer != null) lightBuffer.dispose();
        if (lightTexture != null) lightTexture.dispose();
    }
}
