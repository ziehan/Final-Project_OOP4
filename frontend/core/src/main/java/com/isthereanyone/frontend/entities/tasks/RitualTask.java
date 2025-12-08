package com.isthereanyone.frontend.entities.tasks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.items.ItemType;
import com.isthereanyone.frontend.components.Inventory;

public class RitualTask extends BaseTask {
    private Array<ItemType> requiredItems;
    private boolean[] slotsFilled;
    private BitmapFont font;
    private float slotRadius = 40f;
    private float slotGap = 120f;

    public RitualTask(float x, float y) {
        super(x, y);
        this.debugColor = Color.PURPLE;

        font = new BitmapFont();
        font.getData().setScale(1.2f);

        requiredItems = new Array<>();
        slotsFilled = new boolean[3];

        generateRequirements();
    }

    private void generateRequirements() {
        while (requiredItems.size < 3) {
            ItemType candidate = ItemType.getRandom();
            if (!requiredItems.contains(candidate, true)) {
                requiredItems.add(candidate);
            }
        }
        System.out.println("Ritual Needs: " + requiredItems.toString());
    }

    @Override
    protected void executeLogic() {
        System.out.println("Membuka Altar...");
    }

    @Override
    public boolean updateMinigame(float delta, Viewport viewport, Player player) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) return true;
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            handleInput(viewport, player);
        }

        return isCompleted;
    }

    private void handleInput(Viewport viewport, Player player) {
        Vector2 mousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mousePos);

        float cx = viewport.getCamera().position.x;
        float cy = viewport.getCamera().position.y;

        for (int i = 0; i < 3; i++) {
            if (slotsFilled[i]) continue;

            float slotX = cx - slotGap + (i * slotGap);
            float slotY = cy;

            if (Vector2.dst(mousePos.x, mousePos.y, slotX, slotY) < slotRadius) {
                attemptPlaceItem(i, player);
            }
        }
    }

    private void attemptPlaceItem(int slotIndex, Player player) {
        Inventory inv = player.inventory;
        int selectedSlot = inv.getSelectedSlot();

        if (selectedSlot < inv.getItems().size()) {
            ItemType itemInHand = inv.getItems().get(selectedSlot);
            ItemType itemNeeded = requiredItems.get(slotIndex);

            System.out.println("Trying to place: " + itemInHand + " into " + itemNeeded);

            if (itemInHand == itemNeeded) {
                inv.dropSelectedItem();
                slotsFilled[slotIndex] = true;
                System.out.println("Success!");

                checkWin();
            } else {
                System.out.println("Wrong Item!");
            }
        } else {
            System.out.println("Hand is empty!");
        }
    }

    private void checkWin() {
        if (slotsFilled[0] && slotsFilled[1] && slotsFilled[2]) {
            completeTask();
        }
    }

    @Override
    public void renderMinigame(SpriteBatch batch, ShapeRenderer shape, Viewport viewport) {
        float cx = viewport.getCamera().position.x;
        float cy = viewport.getCamera().position.y;

        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0.3f, 0.2f, 0.1f, 1f);
        shape.rect(cx - 200, cy - 100, 400, 200);

        for (int i = 0; i < 3; i++) {
            float slotX = cx - slotGap + (i * slotGap);
            float slotY = cy;

            if (slotsFilled[i]) shape.setColor(Color.GREEN);
            else shape.setColor(0.1f, 0.1f, 0.1f, 1f);

            shape.circle(slotX, slotY, slotRadius);
            shape.end();
            shape.begin(ShapeRenderer.ShapeType.Line);
            shape.setColor(Color.GRAY);
            shape.circle(slotX, slotY, slotRadius);
            shape.end();
            shape.begin(ShapeRenderer.ShapeType.Filled);
        }
        shape.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        font.setColor(Color.WHITE);
        font.draw(batch, "OFFERING REQUIRED:", cx - 80, cy + 130);

        for (int i = 0; i < 3; i++) {
            float slotX = cx - slotGap + (i * slotGap) - 30;
            float slotY = cy - 50;

            String label = requiredItems.get(i).toString();
            if (slotsFilled[i]) {
                font.setColor(Color.GREEN);
                label = "DONE";
            } else {
                font.setColor(Color.YELLOW);
            }

            font.draw(batch, label, slotX, slotY);
        }

        batch.end();
    }
}
