package com.isthereanyone.frontend.managers;

public class NetworkManager {
    private static NetworkManager instance;

    private NetworkManager(){}

    public static NetworkManager getInstance(){
        if(instance == null){
            instance = new NetworkManager();
        }
        return instance;
    }

    public boolean login(String username, String password){
        System.out.println("[LOGIN] " + username);
        return true;
    }

    public boolean signup(String username, String password) {
        System.out.println("[SIGNUP] " + username);
        return true;
    }

    public boolean loadSaveSlots(String username) {
        System.out.println("[LOAD SLOTS] " + username);
        return true;
    }

    public boolean saveGameData(String username, int slotNumber, SaveSlotManager.SaveSlotData saveData) {
        System.out.println("[SAVE] Slot " + slotNumber);
        return true;
    }

    public void submitScore(int score){
        System.out.println("[SCORE] " + score);
    }
}
