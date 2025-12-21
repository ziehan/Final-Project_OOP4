package com.isthereanyone.frontend.config;

/**
 * Konfigurasi global untuk game
 */
public class GameConfig {
    // Viewport settings
    public static final float VIEWPORT_WIDTH = 640f;
    public static final float VIEWPORT_HEIGHT = 360f;

    public static final String API_BASE_URL = "http://localhost:9090/api";

    // Connection settings
    public static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
    public static final int READ_TIMEOUT = 30000; // 30 seconds

    private GameConfig(){}
}
