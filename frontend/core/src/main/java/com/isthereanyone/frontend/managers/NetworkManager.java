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
        System.out.println("[MOCK] Login request sent for: " + username);
        System.out.println("[MOCK] Server responded: 200 OK");
        return true;
    }

    public void submitScore(int score){
        System.out.println("[MOCK] Score " + score + " sent to server.");
    }
}
