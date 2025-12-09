package com.isthereanyone.frontend.input.commands;

import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.items.ItemType;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.screens.PlayScreen;
import com.isthereanyone.frontend.screens.states.TaskState;

public class InteractCommand implements Command {
    private final PlayScreen screen;

    public InteractCommand(PlayScreen screen) {
        this.screen = screen;
    }

    @Override
    public void execute(Player player, float delta) {
        if (player.position.dst(screen.getWorld().gate.getBounds().x, screen.getWorld().gate.getBounds().y) < 80f) {
            screen.getWorld().gate.interact();
            return;
        }

        for (RitualItem item : screen.getWorld().itemsOnGround) {
            if (!item.isCollected && player.position.dst(item.getBounds().x, item.getBounds().y) < 40f) {
                if (!player.inventory.isFull()) {
                    item.isCollected = true;
                    player.inventory.addItem(item.getType());
                    screen.getWorld().itemsOnGround.removeValue(item, true);
                } else {
                    ItemType itemToDrop = player.inventory.swapItem(item.getType());
                    if (itemToDrop != null) {
                        RitualItem newItem = new RitualItem(itemToDrop, item.getBounds().x, item.getBounds().y);
                        screen.getWorld().itemsOnGround.add(newItem);
                        item.isCollected = true;
                        screen.getWorld().itemsOnGround.removeValue(item, true);
                    }
                }
                return;
            }
        }

        for (BaseTask task : screen.getWorld().tasks) {
            if (!task.isCompleted && player.position.dst(task.getBounds().x, task.getBounds().y) < 60f) {
                task.interact(player);
                screen.setState(new TaskState(screen, task));
                break;
            }
        }
    }
}
