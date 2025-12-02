package com.isthereanyone.frontend.input.commands;

import com.isthereanyone.frontend.entities.Player;

public interface Command {
    // Perintah dieksekusi terhadap Player
    void execute(Player player, float delta);
}

