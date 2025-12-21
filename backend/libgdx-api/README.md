# 🎮 LibGDX API Integration Guide

Panduan lengkap untuk menghubungkan game LibGDX dengan backend.

## 📁 File yang Perlu Di-copy ke Project Frontend

Copy semua file di folder `libgdx-api/` ke project game kamu:

```
libgdx-api/
├── ApiService.java           # Main API service
├── ApiResponseParser.java    # Helper untuk parse response
├── ApiUsageExamples.java     # Contoh penggunaan
└── model/
    ├── User.java             # Model user
    ├── SlotInfo.java         # Model slot info
    ├── SaveData.java         # Model save data lengkap
    ├── GhostData.java        # Model data ghost
    ├── TaskData.java         # Model data task
    └── ItemData.java         # Model data item
```

## ⚙️ Konfigurasi

### Base URL

```java
private static final String BASE_URL = "http://localhost:9090/api";
```

---

## 🔥 PENTING: Masalah yang Harus Diperbaiki di Frontend

### Masalah Utama
1. **Data dari backend TIDAK di-apply ke game** - loadGame() dipanggil tapi data tidak digunakan
2. **Tiled spawn point selalu override** - posisi dari save diabaikan
3. **Ghost, task, inventory tidak di-save/load**

### Solusi

#### 1. Saat SAVE GAME - Simpan Semua Data

```java
public SaveData createSaveData() {
    SaveData save = new SaveData();
    
    // Player
    save.setPlayerHp(player.getHp());
    save.setMaxHp(player.getMaxHp());
    save.setPlayerX(player.getX());
    save.setPlayerY(player.getY());
    save.setPlayerDirection(player.getDirection());
    
    // Level
    save.setCurrentLevel(currentLevel);
    save.setCurrentRoom(currentRoom);
    save.setCurrentMap(currentMapFile);
    
    // Ghost - SIMPAN POSISI GHOST!
    for (Ghost ghost : ghosts) {
        GhostData gd = new GhostData();
        gd.setX(ghost.getX());
        gd.setY(ghost.getY());
        gd.setState(ghost.getState()); // "patrol", "chase", etc
        save.addGhost(gd);
    }
    
    // Tasks - SIMPAN STATUS TASK!
    for (Task task : taskManager.getAllTasks()) {
        TaskData td = new TaskData();
        td.setTaskId(task.getId());
        td.setTaskName(task.getName());
        td.setStatus(task.getStatus()); // "completed", "in_progress"
        td.setProgress(task.getProgress());
        td.setMaxProgress(task.getMaxProgress());
        save.getTasks().add(td);
        
        if (task.isCompleted()) {
            save.addCompletedTask(task.getId());
        }
    }
    
    // Inventory - SIMPAN BARANG!
    for (Item item : inventory.getItems()) {
        ItemData id = new ItemData();
        id.setItemId(item.getId());
        id.setItemName(item.getName());
        id.setItemType(item.getType());
        id.setQuantity(item.getQuantity());
        id.setEquipped(item.isEquipped());
        save.addItem(id);
    }
    save.setCurrentlyHeldItemId(inventory.getHeldItemId());
    
    // PENTING: Set flag untuk spawn dari posisi save
    save.setUseCustomSpawn(true);
    save.setCustomSpawnX(player.getX());
    save.setCustomSpawnY(player.getY());
    save.setSpawnRoom(currentRoom);
    
    // Stats
    save.setPlayTime(gameTime);
    save.setDeathCount(deathCount);
    save.setSaveTimestamp(System.currentTimeMillis());
    
    return save;
}
```

#### 2. Saat LOAD GAME - Apply Semua Data

```java
public void applyLoadedData(SaveData save) {
    // PENTING: Cek apakah harus pakai posisi dari save
    if (save.isUseCustomSpawn()) {
        // JANGAN pakai Tiled spawn point!
        player.setPosition(save.getPlayerX(), save.getPlayerY());
    } else {
        // New game - pakai Tiled spawn point
        player.setPosition(tiledSpawnPoint.x, tiledSpawnPoint.y);
    }
    
    // Player stats
    player.setHp(save.getPlayerHp());
    player.setMaxHp(save.getMaxHp());
    player.setDirection(save.getPlayerDirection());
    
    // Level
    loadMap(save.getCurrentMap());
    currentRoom = save.getCurrentRoom();
    
    // Ghost - RESTORE POSISI GHOST!
    List<GhostData> ghostsData = save.getGhosts();
    for (int i = 0; i < ghostsData.size() && i < ghosts.size(); i++) {
        GhostData gd = ghostsData.get(i);
        ghosts.get(i).setPosition(gd.getX(), gd.getY());
        ghosts.get(i).setState(gd.getState());
    }
    
    // Tasks - RESTORE STATUS TASK!
    for (TaskData td : save.getTasks()) {
        Task task = taskManager.getTask(td.getTaskId());
        if (task != null) {
            task.setStatus(td.getStatus());
            task.setProgress(td.getProgress());
        }
    }
    // Mark completed tasks
    for (String taskId : save.getCompletedTaskIds()) {
        taskManager.markCompleted(taskId);
    }
    
    // Inventory - RESTORE BARANG!
    inventory.clear();
    for (ItemData id : save.getInventoryItems()) {
        Item item = new Item(id.getItemId(), id.getItemName());
        item.setType(id.getItemType());
        item.setQuantity(id.getQuantity());
        item.setEquipped(id.isEquipped());
        inventory.addItem(item);
    }
    if (save.getCurrentlyHeldItemId() != null) {
        inventory.holdItem(save.getCurrentlyHeldItemId());
    }
    
    // Stats
    gameTime = save.getPlayTime();
    deathCount = save.getDeathCount();
}
```

#### 3. Flag untuk New Game vs Load Game

```java
public class GameScreen {
    private boolean isLoadedFromSave = false;
    
    // Dipanggil dari Load Game menu
    public void loadFromSave(int slotId) {
        isLoadedFromSave = true;
        SaveLoadManager.getInstance().loadGame(slotId, new LoadCallback() {
            @Override
            public void onSuccess(SaveData save) {
                applyLoadedData(save);
            }
        });
    }
    
    // Dipanggil dari New Game menu
    public void startNewGame() {
        isLoadedFromSave = false;
        // Reset semua ke default
        player.setPosition(tiledSpawnPoint.x, tiledSpawnPoint.y);
        player.setHp(3);
        // ... reset lainnya
    }
}
```

---

## 📋 Checklist Debug

Tambahkan log di setiap langkah:

```java
// Saat save
Gdx.app.log("SAVE", "Saving player at: " + player.getX() + ", " + player.getY());
Gdx.app.log("SAVE", "Saving " + ghosts.size() + " ghosts");
Gdx.app.log("SAVE", "Saving " + completedTasks.size() + " completed tasks");
Gdx.app.log("SAVE", "Saving " + inventory.size() + " items");

// Saat load
Gdx.app.log("LOAD", "Loaded player position: " + save.getPlayerX() + ", " + save.getPlayerY());
Gdx.app.log("LOAD", "useCustomSpawn: " + save.isUseCustomSpawn());
Gdx.app.log("LOAD", "Loaded " + save.getGhosts().size() + " ghosts");
Gdx.app.log("LOAD", "Loaded " + save.getCompletedTaskIds().size() + " completed tasks");

// Saat apply
Gdx.app.log("APPLY", "Setting player position to: " + x + ", " + y);
Gdx.app.log("APPLY", "Setting player HP to: " + hp);
```

---

## 🌐 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Registrasi |
| POST | `/api/auth/signin` | Login |
| POST | `/api/save` | Simpan game |
| GET | `/api/save/{userId}/{slotId}` | Load game |
| GET | `/api/save/{userId}/slots` | Get semua slot |
| DELETE | `/api/save/{userId}/{slotId}` | Hapus slot |
| GET | `/api/health` | Health check |

**Base URL**: `http://localhost:9090/api`

