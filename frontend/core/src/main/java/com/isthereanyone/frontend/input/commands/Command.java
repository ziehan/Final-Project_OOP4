package com.isthereanyone.frontend.input.commands;

import com.isthereanyone.frontend.entities.Player;

public interface Command {
    void execute(Player player, float delta);
}

