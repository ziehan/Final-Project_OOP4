package com.isthereanyone.frontend.entities.tasks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.entities.Player;

public class StoneRingTask extends BaseTask {
    private float angleOuter = 90f;
    private float angleMiddle = 180f;
    private float angleInner = 270f;
    private float centerX;
    private float centerY;
    private float rOuter = 120;
    private float rMiddle = 80;
    private float rInner = 40;

    public StoneRingTask(float x, float y) {
        super(x, y);
    }

    @Override
    protected void executeLogic() { System.out.println("Membuka Segel Batu..."); }

    @Override
    public boolean updateMinigame(float delta, Viewport viewport, Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) return true;

        centerX = viewport.getCamera().position.x;
        centerY = viewport.getCamera().position.y;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(mousePos);

            float dist = Vector2.dst(mousePos.x, mousePos.y, centerX, centerY);

            if (dist < rInner) rotateInner();
            else if (dist < rMiddle) rotateMiddle();
            else if (dist < rOuter) rotateOuter();

            checkWin();
        }
        return isCompleted;
    }

    private void rotateOuter() {
        angleOuter = (angleOuter + 45) % 360;
        angleMiddle = (angleMiddle - 45 + 360) % 360;
    }
    private void rotateMiddle() {
        angleMiddle = (angleMiddle + 45) % 360;
        angleInner = (angleInner + 45) % 360;
    }
    private void rotateInner() {
        angleInner = (angleInner + 45) % 360;
        angleOuter = (angleOuter - 45 + 360) % 360;
    }

    private void checkWin() {
        if (angleOuter == 0 && angleMiddle == 0 && angleInner == 0) completeTask();
    }

    @Override
    public void renderMinigame(SpriteBatch batch, ShapeRenderer shape, Viewport viewport) {
        // Update titik tengah visual
        centerX = viewport.getCamera().position.x;
        centerY = viewport.getCamera().position.y;

        shape.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3);

        shape.setColor(Color.GRAY);
        if (angleOuter == 0) shape.setColor(Color.GREEN);
        shape.circle(centerX, centerY, rOuter);
        drawPointer(shape, rOuter, angleOuter);

        shape.setColor(Color.GRAY);
        if (angleMiddle == 0) shape.setColor(Color.GREEN);
        shape.circle(centerX, centerY, rMiddle);
        drawPointer(shape, rMiddle, angleMiddle);

        shape.setColor(Color.GRAY);
        if (angleInner == 0) shape.setColor(Color.GREEN);
        shape.circle(centerX, centerY, rInner);
        drawPointer(shape, rInner, angleInner);

        shape.setColor(Color.YELLOW);
        shape.line(centerX, centerY + rOuter + 10, centerX, centerY + rOuter + 40);

        shape.end();
    }

    private void drawPointer(ShapeRenderer shape, float radius, float angleDeg) {
        float rad = MathUtils.degreesToRadians * (angleDeg + 90);
        float x = centerX + MathUtils.cos(rad) * radius;
        float y = centerY + MathUtils.sin(rad) * radius;
        shape.line(centerX, centerY, x, y);
    }
}
