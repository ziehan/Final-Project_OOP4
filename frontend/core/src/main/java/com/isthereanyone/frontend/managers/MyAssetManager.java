package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class MyAssetManager implements Disposable {
    private static MyAssetManager instance;
    private final AssetManager manager;

    private MyAssetManager() {
        manager = new AssetManager();
    }

    public static MyAssetManager getInstance(){
        if(instance == null){
            instance = new MyAssetManager();
        }
        return instance;
    }

    public void loadAssets(){
        manager.load("she.png", Texture.class);

        manager.load("Unarmed_Idle_with_shadow.png", Texture.class);
        manager.load("Unarmed_Walk_with_shadow.png", Texture.class);
        manager.load("Unarmed_Run_with_shadow.png", Texture.class);
        manager.load("Unarmed_Hurt_with_shadow.png", Texture.class);
        manager.load("Unarmed_Death_with_shadow.png", Texture.class);

        manager.load("Vampires3_Idle_with_shadow.png", Texture.class);
        manager.load("Vampires3_Walk_with_shadow.png", Texture.class);
        manager.load("Vampires3_Run_with_shadow.png", Texture.class);
        manager.load("Vampires3_Attack_with_shadow.png", Texture.class);
    }

    public void finishLoading(){
        manager.finishLoading();
    }

    public <T> T get(String fileName){
        return manager.get(fileName);
    }

    @Override
    public void dispose() {
        manager.dispose();
    }
}
