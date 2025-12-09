package com.isthereanyone.frontend.input.commands;

import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.items.ItemType;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.screens.PlayScreen;

public class DropCommand implements Command {
    private final PlayScreen screen;

    public DropCommand(PlayScreen screen) {
        this.screen = screen;
    }

    @Override
    public void execute(Player player, float delta) {
        ItemType dropped = player.inventory.dropSelectedItem();
        if (dropped != null) {
            screen.getWorld().itemsOnGround.add(new RitualItem(dropped, player.position.x + 20, player.position.y));
            System.out.println("Dropped: " + dropped);
        }
    }
}
