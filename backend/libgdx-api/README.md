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
    └── SlotInfo.java         # Model slot info
```

## ⚙️ Konfigurasi

### 1. Sesuaikan Package Name

Ubah package name di semua file sesuai dengan project kamu:
```java
// Dari:
package com.isthereanyone.api;

// Ke (sesuaikan):
package com.yourgame.api;
```

### 2. Sesuaikan Base URL

Di file `ApiService.java`, ubah URL jika backend di-deploy:
```java
// Development (localhost)
private static final String BASE_URL = "http://localhost:9090/api";

// Production (contoh)
private static final String BASE_URL = "https://api.yourgame.com/api";
```

## 🚀 Cara Penggunaan

### 1. Inisialisasi ApiService

```java
public class MyGame extends Game {
    private ApiService api;
    
    @Override
    public void create() {
        api = new ApiService();
        
        // Cek koneksi server
        api.healthCheck(new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                System.out.println("Server connected!");
            }
            
            @Override
            public void onError(String error) {
                System.out.println("Server offline: " + error);
            }
        });
    }
}
```

### 2. Login

```java
api.signin("username", "password", new ApiService.ApiCallback() {
    @Override
    public void onSuccess(JsonValue response) {
        User user = ApiResponseParser.parseUserFromAuthResponse(response);
        
        // Simpan user untuk digunakan nanti
        GameManager.setCurrentUser(user);
        
        // Pindah ke main menu
        game.setScreen(new MainMenuScreen());
    }
    
    @Override
    public void onError(String error) {
        // Tampilkan error di UI
        loginDialog.showError(error);
    }
});
```

### 3. Save Game

```java
// Buat save data
Json json = new Json();
json.setOutputType(JsonWriter.OutputType.json);

Map<String, Object> saveData = new HashMap<>();
saveData.put("playerState", Map.of(
    "currentMap", "level_2",
    "posX", player.getX(),
    "posY", player.getY(),
    "health", player.getHealth()
));
saveData.put("stats", Map.of(
    "allTimeDeathCount", stats.getDeathCount(),
    "allTimeCompletedTask", stats.getCompletedTasks()
));

String saveDataJson = json.toJson(saveData);

// Simpan ke slot 1
api.saveGame(currentUser.getUsername(), 1, saveDataJson, new ApiService.ApiCallback() {
    @Override
    public void onSuccess(JsonValue response) {
        showNotification("Game Saved!");
    }
    
    @Override
    public void onError(String error) {
        showNotification("Failed to save: " + error);
    }
});
```

### 4. Load Game

```java
api.loadGame(currentUser.getUsername(), slotId, new ApiService.ApiCallback() {
    @Override
    public void onSuccess(JsonValue response) {
        JsonValue saveData = ApiResponseParser.parseSaveData(response);
        
        // Apply ke game
        JsonValue playerState = saveData.get("playerState");
        String map = playerState.getString("currentMap");
        float x = playerState.getFloat("posX");
        float y = playerState.getFloat("posY");
        int health = playerState.getInt("health");
        
        // Load map dan posisi player
        loadMap(map);
        player.setPosition(x, y);
        player.setHealth(health);
    }
    
    @Override
    public void onError(String error) {
        showNotification("Failed to load: " + error);
    }
});
```

### 5. Tampilkan Save Slots di Menu

```java
api.getAllSlots(currentUser.getUsername(), new ApiService.ApiCallback() {
    @Override
    public void onSuccess(JsonValue response) {
        List<SlotInfo> slots = ApiResponseParser.parseSlotList(response);
        
        for (SlotInfo slot : slots) {
            if (slot.isEmpty()) {
                // Tampilkan "Empty Slot"
                slotButtons[slot.getSlotId()].setText("Empty");
            } else {
                // Tampilkan info slot
                slotButtons[slot.getSlotId()].setText(
                    slot.getCurrentMap() + "\n" +
                    "Deaths: " + slot.getAllTimeDeathCount()
                );
            }
        }
    }
    
    @Override
    public void onError(String error) {
        System.out.println("Error: " + error);
    }
});
```

## 📋 API Endpoints

| Method | Endpoint | Fungsi |
|--------|----------|--------|
| POST | `/api/auth/signup` | Registrasi user baru |
| POST | `/api/auth/signin` | Login user |
| GET | `/api/auth/check/username/{username}` | Cek username tersedia |
| GET | `/api/auth/check/email/{email}` | Cek email tersedia |
| GET | `/api/auth/user/{username}` | Get user info |
| POST | `/api/save` | Simpan game |
| GET | `/api/save/{userId}/{slotId}` | Load game |
| GET | `/api/save/{userId}/slots` | Get semua slot |
| DELETE | `/api/save/{userId}/{slotId}` | Hapus slot |
| DELETE | `/api/save/{userId}` | Hapus semua slot |
| GET | `/api/health` | Health check |

## ⚠️ Penting

1. **Asynchronous**: Semua API call bersifat async. Response dijalankan di GL thread via `Gdx.app.postRunnable()`.

2. **Error Handling**: Selalu implement `onError()` untuk menangani error.

3. **Internet Permission**: Pastikan ada permission di Android manifest:
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

4. **Thread Safety**: Jangan akses OpenGL resources langsung di callback. Gunakan `Gdx.app.postRunnable()` jika perlu.

## 🌐 Base URL

- **Development**: `http://localhost:9090/api`
- **Production**: Sesuaikan dengan URL server yang di-deploy

