package com.isthereanyone.frontend.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.isthereanyone.frontend.entities.Player;
import com.isthereanyone.frontend.entities.ghost.Ghost;
import com.isthereanyone.frontend.entities.tasks.BaseTask;
import com.isthereanyone.frontend.entities.items.RitualItem;
import com.isthereanyone.frontend.network.NetworkCallback;
import com.isthereanyone.frontend.network.dto.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameSaveManager - Handles save/load game state with full data structure
 * Supports: Player, Ghost positions, Task status, Inventory, Spawn control
 */
public class GameSaveManager {
    private static GameSaveManager instance;

    // Game state tracking
    private boolean isLoadedFromSave = false;
    private SaveData currentSaveData = null;
    private int currentSlotId = -1;
    private long gameStartTime = 0;
    private int deathCount = 0;

    private GameSaveManager() {
        gameStartTime = System.currentTimeMillis();
    }

    public static GameSaveManager getInstance() {
        if (instance == null) {
            instance = new GameSaveManager();
        }
        return instance;
    }

    // ==================== SAVE GAME ====================

    /**
     * Create SaveData from current game state
     */
    public SaveData createSaveData(GameWorld world) {
        SaveData save = new SaveData();

        Player player = world.player;
        Ghost ghost = world.ghost;

        // Player Data
        save.setPlayerHp(player.health);
        save.setMaxHp(3); // Default max HP
        save.setPlayerX(player.position.x);
        save.setPlayerY(player.position.y);
        save.setHiding(player.isHidden);

        Gdx.app.log("SAVE", "Saving player at: " + player.position.x + ", " + player.position.y);

        // Ghost Data
        if (ghost != null) {
            GhostData gd = new GhostData();
            gd.setX(ghost.getPosition().x);
            gd.setY(ghost.getPosition().y);
            gd.setState("patrol"); // Default state
            save.addGhost(gd);
            Gdx.app.log("SAVE", "Saving ghost at: " + ghost.getPosition().x + ", " + ghost.getPosition().y);
        }

        // Task Data
        int completedCount = 0;
        for (BaseTask task : world.tasks) {
            TaskData td = new TaskData();
            td.setTaskId(task.getType() + "_" + task.getPosition().x + "_" + task.getPosition().y);
            td.setTaskName(task.getType());
            td.setStatus(task.isCompleted ? "completed" : "not_started");
            td.setProgress(task.isCompleted ? 1 : 0);
            td.setMaxProgress(1);
            save.getTasks().add(td);

            if (task.isCompleted) {
                save.addCompletedTask(td.getTaskId());
                completedCount++;
            }
        }
        save.setTotalTasksCompleted(completedCount);
        save.setTotalTasksRequired(world.tasks.size);
        Gdx.app.log("SAVE", "Saving " + completedCount + "/" + world.tasks.size + " completed tasks");

        // Inventory Data
        if (player.inventory != null) {
            for (RitualItem item : world.itemsOnGround) {
                if (item.isCollected) {
                    ItemData id = new ItemData();
                    id.setItemId(item.getType().name());
                    id.setItemName(item.getType().name());
                    id.setItemType("quest_item");
                    id.setQuantity(1);
                    save.addItem(id);
                }
            }
            Gdx.app.log("SAVE", "Saving " + save.getInventoryItems().size() + " inventory items");
        }

        // Spawn Settings - PENTING!
        save.setUseCustomSpawn(true); // Load dari save = pakai posisi dari save
        save.setCustomSpawnX(player.position.x);
        save.setCustomSpawnY(player.position.y);
        save.setCurrentMap("Tilemap.tmx");

        // Stats
        save.setPlayTime(System.currentTimeMillis() - gameStartTime);
        save.setDeathCount(deathCount);
        save.setSaveTimestamp(System.currentTimeMillis());
        save.setLastPlayedTimestamp(System.currentTimeMillis());

        return save;
    }

    /**
     * Save game to backend
     */
    public void saveGame(int slotId, GameWorld world, NetworkCallback<Boolean> callback) {
        SaveData saveData = createSaveData(world);

        // Convert SaveData to Map for backend
        Map<String, Object> saveMap = convertSaveDataToMap(saveData);

        NetworkManager.getInstance().saveGame(slotId, saveMap, new NetworkCallback<ApiResponse<GameSaveResponse>>() {
            @Override
            public void onSuccess(ApiResponse<GameSaveResponse> result) {
                if (result.isSuccess()) {
                    currentSlotId = slotId;
                    currentSaveData = saveData;
                    Gdx.app.log("SAVE", "Game saved successfully to slot " + slotId);
                    callback.onSuccess(true);
                } else {
                    Gdx.app.error("SAVE", "Save failed: " + result.getMessage());
                    callback.onFailure(result.getMessage());
                }
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("SAVE", "Save failed: " + error);
                callback.onFailure(error);
            }
        });
    }

    // ==================== LOAD GAME ====================

    /**
     * Load game from backend
     */
    public void loadGame(int slotId, NetworkCallback<SaveData> callback) {
        NetworkManager.getInstance().loadGame(slotId, new NetworkCallback<ApiResponse<GameSaveResponse>>() {
            @Override
            public void onSuccess(ApiResponse<GameSaveResponse> result) {
                if (result.isSuccess() && result.getData() != null) {
                    SaveData saveData = parseSaveData(result.getData().getSaveData());
                    currentSaveData = saveData;
                    currentSlotId = slotId;
                    isLoadedFromSave = true;

                    Gdx.app.log("LOAD", "Game loaded from slot " + slotId);
                    Gdx.app.log("LOAD", "useCustomSpawn: " + saveData.isUseCustomSpawn());
                    Gdx.app.log("LOAD", "Player position: " + saveData.getPlayerX() + ", " + saveData.getPlayerY());

                    callback.onSuccess(saveData);
                } else {
                    callback.onFailure(result.getMessage() != null ? result.getMessage() : "No save data");
                }
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("LOAD", "Load failed: " + error);
                callback.onFailure(error);
            }
        });
    }

    /**
     * Apply loaded data to game world
     */
    public void applyLoadedData(SaveData save, GameWorld world, float tiledSpawnX, float tiledSpawnY) {
        Player player = world.player;
        Ghost ghost = world.ghost;

        // PENTING: Cek apakah harus pakai posisi dari save
        if (save.isUseCustomSpawn()) {
            // Pakai posisi dari save
            player.position.set(save.getPlayerX(), save.getPlayerY());
            Gdx.app.log("APPLY", "Using save position: " + save.getPlayerX() + ", " + save.getPlayerY());
        } else {
            // New game - pakai Tiled spawn point
            player.position.set(tiledSpawnX, tiledSpawnY);
            Gdx.app.log("APPLY", "Using Tiled spawn: " + tiledSpawnX + ", " + tiledSpawnY);
        }

        // Player stats
        player.health = save.getPlayerHp();
        player.isHidden = save.isHiding();
        Gdx.app.log("APPLY", "Player HP: " + player.health);

        // Ghost positions
        List<GhostData> ghostsData = save.getGhosts();
        if (ghost != null && ghostsData != null && !ghostsData.isEmpty()) {
            GhostData gd = ghostsData.get(0);
            ghost.getPosition().set(gd.getX(), gd.getY());
            Gdx.app.log("APPLY", "Ghost position: " + gd.getX() + ", " + gd.getY());
        }

        // Task status - mark completed tasks
        if (save.getCompletedTaskIds() != null) {
            for (String taskId : save.getCompletedTaskIds()) {
                for (BaseTask task : world.tasks) {
                    String currentTaskId = task.getType() + "_" + task.getPosition().x + "_" + task.getPosition().y;
                    if (currentTaskId.equals(taskId)) {
                        task.isCompleted = true;
                    }
                }
            }
            Gdx.app.log("APPLY", "Restored " + save.getCompletedTaskIds().size() + " completed tasks");
        }

        // Inventory - mark collected items
        if (save.getInventoryItems() != null) {
            for (ItemData itemData : save.getInventoryItems()) {
                for (RitualItem item : world.itemsOnGround) {
                    if (item.getType().name().equals(itemData.getItemId())) {
                        item.isCollected = true;
                    }
                }
            }
            Gdx.app.log("APPLY", "Restored " + save.getInventoryItems().size() + " inventory items");
        }

        // Stats
        if (save.getPlayTime() > 0) {
            gameStartTime = System.currentTimeMillis() - save.getPlayTime();
        }
        deathCount = save.getDeathCount();
    }

    // ==================== NEW GAME ====================

    /**
     * Start new game - reset all state
     */
    public void startNewGame() {
        isLoadedFromSave = false;
        currentSaveData = null;
        currentSlotId = -1;
        gameStartTime = System.currentTimeMillis();
        deathCount = 0;
        Gdx.app.log("GAME", "Starting new game - using Tiled spawn point");
    }

    // ==================== HELPER METHODS ====================

    private Map<String, Object> convertSaveDataToMap(SaveData save) {
        Map<String, Object> map = new HashMap<>();

        // Player state
        Map<String, Object> playerState = new HashMap<>();
        playerState.put("hp", save.getPlayerHp());
        playerState.put("maxHp", save.getMaxHp());
        playerState.put("posX", save.getPlayerX());
        playerState.put("posY", save.getPlayerY());
        playerState.put("direction", save.getPlayerDirection());
        playerState.put("isHiding", save.isHiding());
        map.put("playerState", playerState);

        // Ghosts
        List<Map<String, Object>> ghostsList = new ArrayList<>();
        for (GhostData gd : save.getGhosts()) {
            Map<String, Object> ghostMap = new HashMap<>();
            ghostMap.put("x", gd.getX());
            ghostMap.put("y", gd.getY());
            ghostMap.put("state", gd.getState());
            ghostMap.put("alertLevel", gd.getAlertLevel());
            ghostsList.add(ghostMap);
        }
        map.put("ghosts", ghostsList);

        // Tasks
        List<Map<String, Object>> tasksList = new ArrayList<>();
        for (TaskData td : save.getTasks()) {
            Map<String, Object> taskMap = new HashMap<>();
            taskMap.put("taskId", td.getTaskId());
            taskMap.put("taskName", td.getTaskName());
            taskMap.put("status", td.getStatus());
            taskMap.put("progress", td.getProgress());
            taskMap.put("maxProgress", td.getMaxProgress());
            tasksList.add(taskMap);
        }
        map.put("tasks", tasksList);
        map.put("completedTaskIds", save.getCompletedTaskIds());
        map.put("totalTasksCompleted", save.getTotalTasksCompleted());
        map.put("totalTasksRequired", save.getTotalTasksRequired());

        // Inventory
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (ItemData id : save.getInventoryItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("itemId", id.getItemId());
            itemMap.put("itemName", id.getItemName());
            itemMap.put("itemType", id.getItemType());
            itemMap.put("quantity", id.getQuantity());
            itemMap.put("isEquipped", id.isEquipped());
            itemsList.add(itemMap);
        }
        map.put("inventoryItems", itemsList);
        map.put("currentlyHeldItemId", save.getCurrentlyHeldItemId());

        // Spawn settings
        map.put("useCustomSpawn", save.isUseCustomSpawn());
        map.put("customSpawnX", save.getCustomSpawnX());
        map.put("customSpawnY", save.getCustomSpawnY());
        map.put("currentMap", save.getCurrentMap());

        // Stats
        map.put("playTime", save.getPlayTime());
        map.put("deathCount", save.getDeathCount());
        map.put("saveTimestamp", save.getSaveTimestamp());

        return map;
    }

    @SuppressWarnings("unchecked")
    private SaveData parseSaveData(Map<String, Object> map) {
        SaveData save = new SaveData();

        if (map == null) {
            Gdx.app.log("PARSE", "Save data map is null");
            return save;
        }

        // Debug: Log all keys in the map
        Gdx.app.log("PARSE", "Parsing save data with keys: " + map.keySet());

        // Player state - check multiple possible structures
        if (map.containsKey("playerState")) {
            Object psObj = map.get("playerState");
            if (psObj instanceof Map) {
                Map<String, Object> ps = (Map<String, Object>) psObj;
                Gdx.app.log("PARSE", "playerState keys: " + ps.keySet());

                // Debug: log raw values
                Gdx.app.log("PARSE", "Raw posX value: " + ps.get("posX") + " (type: " +
                    (ps.get("posX") != null ? ps.get("posX").getClass().getName() : "null") + ")");
                Gdx.app.log("PARSE", "Raw posY value: " + ps.get("posY") + " (type: " +
                    (ps.get("posY") != null ? ps.get("posY").getClass().getName() : "null") + ")");

                save.setPlayerHp(getInt(ps, "hp", 3));
                save.setMaxHp(getInt(ps, "maxHp", 3));
                save.setPlayerX(getFloat(ps, "posX", 0));
                save.setPlayerY(getFloat(ps, "posY", 0));
                save.setPlayerDirection(getString(ps, "direction", "down"));
                save.setHiding(getBool(ps, "isHiding", false));

                Gdx.app.log("PARSE", "Parsed player position: " + save.getPlayerX() + ", " + save.getPlayerY());

                // Also set spawn from playerState if useCustomSpawn not set elsewhere
                if (save.getPlayerX() != 0 || save.getPlayerY() != 0) {
                    save.setUseCustomSpawn(true);
                    save.setCustomSpawnX(save.getPlayerX());
                    save.setCustomSpawnY(save.getPlayerY());
                    Gdx.app.log("PARSE", "Set useCustomSpawn=true from playerState");
                }
            }
        }

        // Also check for flat structure (player data at root level)
        if (!map.containsKey("playerState")) {
            save.setPlayerHp(getInt(map, "playerHp", getInt(map, "hp", 3)));
            save.setPlayerX(getFloat(map, "playerX", getFloat(map, "posX", 0)));
            save.setPlayerY(getFloat(map, "playerY", getFloat(map, "posY", 0)));

            if (save.getPlayerX() != 0 || save.getPlayerY() != 0) {
                save.setUseCustomSpawn(true);
                save.setCustomSpawnX(save.getPlayerX());
                save.setCustomSpawnY(save.getPlayerY());
            }
        }

        // Ghosts
        if (map.containsKey("ghosts")) {
            Object ghostsObj = map.get("ghosts");
            if (ghostsObj instanceof List) {
                for (Object ghostObj : (List<?>) ghostsObj) {
                    if (ghostObj instanceof Map) {
                        Map<String, Object> gm = (Map<String, Object>) ghostObj;
                        GhostData gd = new GhostData();
                        gd.setX(getFloat(gm, "x", 0));
                        gd.setY(getFloat(gm, "y", 0));
                        gd.setState(getString(gm, "state", "patrol"));
                        gd.setAlertLevel(getInt(gm, "alertLevel", 0));
                        save.addGhost(gd);
                    }
                }
            }
        }

        // Tasks
        if (map.containsKey("tasks")) {
            Object tasksObj = map.get("tasks");
            if (tasksObj instanceof List) {
                for (Object taskObj : (List<?>) tasksObj) {
                    if (taskObj instanceof Map) {
                        Map<String, Object> tm = (Map<String, Object>) taskObj;
                        TaskData td = new TaskData();
                        td.setTaskId(getString(tm, "taskId", ""));
                        td.setTaskName(getString(tm, "taskName", ""));
                        td.setStatus(getString(tm, "status", "not_started"));
                        td.setProgress(getInt(tm, "progress", 0));
                        td.setMaxProgress(getInt(tm, "maxProgress", 1));
                        save.getTasks().add(td);
                    }
                }
            }
        }

        if (map.containsKey("completedTaskIds")) {
            Object rawTaskIds = map.get("completedTaskIds");
            List<String> completedIds = new ArrayList<>();

            if (rawTaskIds instanceof List) {
                for (Object item : (List<?>) rawTaskIds) {
                    if (item instanceof String) {
                        completedIds.add((String) item);
                    } else if (item instanceof Map) {
                        // Backend mungkin mengembalikan sebagai object
                        Map<?, ?> taskMap = (Map<?, ?>) item;
                        Object taskId = taskMap.get("taskId");
                        if (taskId != null) {
                            completedIds.add(taskId.toString());
                        }
                    } else if (item != null) {
                        completedIds.add(item.toString());
                    }
                }
            }
            save.setCompletedTaskIds(completedIds);
        }

        save.setTotalTasksCompleted(getInt(map, "totalTasksCompleted", 0));
        save.setTotalTasksRequired(getInt(map, "totalTasksRequired", 0));

        // Inventory
        if (map.containsKey("inventoryItems")) {
            Object itemsObj = map.get("inventoryItems");
            if (itemsObj instanceof List) {
                for (Object itemObj : (List<?>) itemsObj) {
                    if (itemObj instanceof Map) {
                        Map<String, Object> im = (Map<String, Object>) itemObj;
                        ItemData id = new ItemData();
                        id.setItemId(getString(im, "itemId", ""));
                        id.setItemName(getString(im, "itemName", ""));
                        id.setItemType(getString(im, "itemType", ""));
                        id.setQuantity(getInt(im, "quantity", 1));
                        id.setEquipped(getBool(im, "isEquipped", false));
                        save.addItem(id);
                    }
                }
            }
        }
        save.setCurrentlyHeldItemId(getString(map, "currentlyHeldItemId", null));

        // Spawn settings - try multiple keys
        boolean hasCustomSpawn = getBool(map, "useCustomSpawn", false);
        float customX = getFloat(map, "customSpawnX", 0);
        float customY = getFloat(map, "customSpawnY", 0);

        // If customSpawn coords are 0 but playerState has coords, use those
        if ((customX == 0 && customY == 0) && (save.getPlayerX() != 0 || save.getPlayerY() != 0)) {
            customX = save.getPlayerX();
            customY = save.getPlayerY();
            hasCustomSpawn = true;
        }

        save.setUseCustomSpawn(hasCustomSpawn);
        save.setCustomSpawnX(customX);
        save.setCustomSpawnY(customY);
        save.setCurrentMap(getString(map, "currentMap", "Tilemap.tmx"));

        Gdx.app.log("PARSE", "Final spawn settings - useCustomSpawn: " + save.isUseCustomSpawn() +
                    ", pos: " + save.getCustomSpawnX() + ", " + save.getCustomSpawnY());

        // Stats
        save.setPlayTime(getLong(map, "playTime", 0));
        save.setDeathCount(getInt(map, "deathCount", 0));
        save.setSaveTimestamp(getLong(map, "saveTimestamp", 0));

        return save;
    }

    // Type-safe getters
    private int getInt(Map<String, Object> map, String key, int defaultVal) {
        if (map.containsKey(key) && map.get(key) != null) {
            Object val = map.get(key);
            if (val instanceof Number) return ((Number) val).intValue();
        }
        return defaultVal;
    }

    private float getFloat(Map<String, Object> map, String key, float defaultVal) {
        if (map.containsKey(key) && map.get(key) != null) {
            Object val = map.get(key);
            if (val instanceof Number) return ((Number) val).floatValue();
        }
        return defaultVal;
    }

    private long getLong(Map<String, Object> map, String key, long defaultVal) {
        if (map.containsKey(key) && map.get(key) != null) {
            Object val = map.get(key);
            if (val instanceof Number) return ((Number) val).longValue();
        }
        return defaultVal;
    }

    private boolean getBool(Map<String, Object> map, String key, boolean defaultVal) {
        if (map.containsKey(key) && map.get(key) != null) {
            Object val = map.get(key);
            if (val instanceof Boolean) return (Boolean) val;
        }
        return defaultVal;
    }

    private String getString(Map<String, Object> map, String key, String defaultVal) {
        if (map.containsKey(key) && map.get(key) != null) {
            return map.get(key).toString();
        }
        return defaultVal;
    }

    // ==================== GETTERS ====================

    public boolean isLoadedFromSave() {
        return isLoadedFromSave;
    }

    public SaveData getCurrentSaveData() {
        return currentSaveData;
    }

    public int getCurrentSlotId() {
        return currentSlotId;
    }

    public void incrementDeathCount() {
        deathCount++;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public long getPlayTime() {
        return System.currentTimeMillis() - gameStartTime;
    }
}

