package com.isthereanyone.frontend.input.commands;

import com.isthereanyone.frontend.entities.Player;

public class SelectSlotCommand implements Command {
    private final int slotIndex;

    public SelectSlotCommand(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    @Override
    public void execute(Player player, float delta) {
        player.inventory.setSelectedSlot(slotIndex);
    }
}
