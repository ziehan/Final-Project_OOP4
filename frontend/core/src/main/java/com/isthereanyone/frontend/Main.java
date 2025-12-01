package com.isthereanyone.frontend;

import com.badlogic.gdx.Game;
import com.isthereanyone.frontend.managers.MyAssetManager;
import com.isthereanyone.frontend.managers.ScreenManager;
import com.isthereanyone.frontend.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    @Override
    public void create() {
        MyAssetManager.getInstance().loadAssets();
        MyAssetManager.getInstance().finishLoading();
        ScreenManager.getInstance().initialize(this);
        ScreenManager.getInstance().setScreen(new MainMenuScreen());
    }

    @Override
    public void dispose(){
        super.dispose();
        MyAssetManager.getInstance().dispose();
    }
}
