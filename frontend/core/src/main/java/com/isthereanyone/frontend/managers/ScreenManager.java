package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager {
    public static ScreenManager instance;
    private Game game;

    private ScreenManager(){}

    public static ScreenManager getInstance(){
        if(instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    public void initialize(Game game){
        this.game = game;
    }

    public void setScreen(Screen screen){
        if(game != null){
            game.setScreen(screen);
        }
    }
}
