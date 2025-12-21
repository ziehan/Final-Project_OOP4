package com.isthereanyone.api;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.isthereanyone.api.model.SlotInfo;
import com.isthereanyone.api.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =====================================================
 * CONTOH PENGGUNAAN API SERVICE UNTUK LIBGDX GAME
 * =====================================================
 *
 * File ini berisi contoh-contoh cara menggunakan ApiService
 * untuk berkomunikasi dengan backend.
 *
 * PENTING: Semua API call bersifat asynchronous!
 * Response akan diproses di GL thread melalui Gdx.app.postRunnable()
 */
public class ApiUsageExamples {

    private ApiService api;
    private User currentUser;  // Simpan user yang sedang login

    public ApiUsageExamples() {
        api = new ApiService();
    }

    // ==================== AUTH EXAMPLES ====================

    /**
     * Contoh: Registrasi User Baru
     */
    public void exampleSignup() {
        api.signup("player123", "player@email.com", "password123", "Player One",
            new ApiService.ApiCallback() {
                @Override
                public void onSuccess(JsonValue response) {
                    // Parse user dari response
                    currentUser = ApiResponseParser.parseUserFromAuthResponse(response);

                    System.out.println("Registrasi berhasil!");
                    System.out.println("User: " + currentUser.getUsername());
                    System.out.println("Email: " + currentUser.getEmail());

                    // Lanjut ke game/main menu
                    // goToMainMenu();
                }

                @Override
                public void onError(String error) {
                    System.out.println("Registrasi gagal: " + error);
                    // Tampilkan error ke user
                    // showErrorDialog(error);
                }
            });
    }

    /**
     * Contoh: Login User
     */
    public void exampleSignin() {
        api.signin("player123", "password123", new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                // Parse user dari response
                currentUser = ApiResponseParser.parseUserFromAuthResponse(response);

                System.out.println("Login berhasil!");
                System.out.println("Selamat datang, " + currentUser.getDisplayName());

                // Simpan user ID untuk save game
                String userId = currentUser.getUsername();

                // Lanjut ke game/main menu
                // goToMainMenu();
            }

            @Override
            public void onError(String error) {
                System.out.println("Login gagal: " + error);
                // Tampilkan error (misal: password salah)
            }
        });
    }

    /**
     * Contoh: Cek Username Tersedia (untuk form registrasi)
     */
    public void exampleCheckUsername(String username) {
        api.checkUsername(username, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                boolean exists = ApiResponseParser.getBooleanData(response);

                if (exists) {
                    System.out.println("Username sudah digunakan!");
                    // Tampilkan warning di form
                } else {
                    System.out.println("Username tersedia!");
                    // Tampilkan checkmark hijau
                }
            }

            @Override
            public void onError(String error) {
                System.out.println("Gagal cek username: " + error);
            }
        });
    }

    // ==================== GAME SAVE EXAMPLES ====================

    /**
     * Contoh: Save Game
     * Simpan state game ke slot tertentu (1-3)
     */
    public void exampleSaveGame() {
        if (currentUser == null) {
            System.out.println("User belum login!");
            return;
        }

        // Buat save data (sesuaikan dengan game state kamu)
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);

        // Contoh struktur save data
        Map<String, Object> saveData = new HashMap<>();

        // Player state
        Map<String, Object> playerState = new HashMap<>();
        playerState.put("currentMap", "forest_level");
        playerState.put("posX", 100.5f);
        playerState.put("posY", 200.3f);
        playerState.put("health", 85);
        playerState.put("maxHealth", 100);
        saveData.put("playerState", playerState);

        // Stats
        Map<String, Object> stats = new HashMap<>();
        stats.put("allTimeDeathCount", 5);
        stats.put("allTimeCompletedTask", 12);
        stats.put("playTime", 3600);  // dalam detik
        saveData.put("stats", stats);

        // Inventory (contoh)
        String[] inventory = {"sword", "health_potion", "key_gold"};
        saveData.put("inventory", inventory);

        // Convert ke JSON string
        String saveDataJson = json.toJson(saveData);

        // Simpan ke slot 1
        int slotId = 1;

        api.saveGame(currentUser.getUsername(), slotId, saveDataJson, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                System.out.println("Game berhasil disimpan!");
                String message = ApiResponseParser.getMessage(response);
                // Tampilkan notifikasi sukses
            }

            @Override
            public void onError(String error) {
                System.out.println("Gagal menyimpan: " + error);
                // Tampilkan error dialog
            }
        });
    }

    /**
     * Contoh: Load Game dari slot
     */
    public void exampleLoadGame(int slotId) {
        if (currentUser == null) {
            System.out.println("User belum login!");
            return;
        }

        api.loadGame(currentUser.getUsername(), slotId, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                System.out.println("Game berhasil dimuat!");

                // Parse save data
                JsonValue saveData = ApiResponseParser.parseSaveData(response);

                if (saveData != null) {
                    // Get player state
                    JsonValue playerState = saveData.get("playerState");
                    if (playerState != null) {
                        String currentMap = playerState.getString("currentMap", "default");
                        float posX = playerState.getFloat("posX", 0);
                        float posY = playerState.getFloat("posY", 0);
                        int health = playerState.getInt("health", 100);

                        System.out.println("Map: " + currentMap);
                        System.out.println("Position: (" + posX + ", " + posY + ")");
                        System.out.println("Health: " + health);

                        // Apply ke game
                        // player.setPosition(posX, posY);
                        // player.setHealth(health);
                        // loadMap(currentMap);
                    }

                    // Get stats
                    JsonValue stats = saveData.get("stats");
                    if (stats != null) {
                        int deathCount = stats.getInt("allTimeDeathCount", 0);
                        int completedTasks = stats.getInt("allTimeCompletedTask", 0);

                        // Apply stats
                        // gameStats.setDeathCount(deathCount);
                    }

                    // Get inventory
                    JsonValue inventory = saveData.get("inventory");
                    if (inventory != null && inventory.isArray()) {
                        for (JsonValue item : inventory) {
                            String itemName = item.asString();
                            // playerInventory.addItem(itemName);
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                System.out.println("Gagal memuat game: " + error);
                // Mungkin slot kosong atau error lain
            }
        });
    }

    /**
     * Contoh: Tampilkan daftar save slots di menu
     */
    public void exampleGetAllSlots() {
        if (currentUser == null) {
            System.out.println("User belum login!");
            return;
        }

        api.getAllSlots(currentUser.getUsername(), new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                // Parse list of slots
                List<SlotInfo> slots = ApiResponseParser.parseSlotList(response);

                System.out.println("=== SAVE SLOTS ===");
                for (SlotInfo slot : slots) {
                    if (slot.isEmpty()) {
                        System.out.println("Slot " + slot.getSlotId() + ": [EMPTY]");
                    } else {
                        System.out.println("Slot " + slot.getSlotId() + ":");
                        System.out.println("  - Map: " + slot.getCurrentMap());
                        System.out.println("  - Deaths: " + slot.getAllTimeDeathCount());
                        System.out.println("  - Tasks: " + slot.getAllTimeCompletedTask());
                        System.out.println("  - Last Updated: " + slot.getLastUpdated());
                    }
                }

                // Update UI dengan list slots
                // saveSlotMenu.updateSlots(slots);
            }

            @Override
            public void onError(String error) {
                System.out.println("Gagal memuat slots: " + error);
            }
        });
    }

    /**
     * Contoh: Hapus save slot
     */
    public void exampleDeleteSlot(int slotId) {
        if (currentUser == null) return;

        api.deleteSlot(currentUser.getUsername(), slotId, new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                System.out.println("Slot " + slotId + " berhasil dihapus!");
                // Refresh UI
            }

            @Override
            public void onError(String error) {
                System.out.println("Gagal menghapus: " + error);
            }
        });
    }

    // ==================== HEALTH CHECK ====================

    /**
     * Contoh: Cek apakah server online
     * Panggil ini saat game start untuk cek koneksi
     */
    public void exampleHealthCheck() {
        api.healthCheck(new ApiService.ApiCallback() {
            @Override
            public void onSuccess(JsonValue response) {
                JsonValue data = response.get("data");
                String status = data.getString("status", "UNKNOWN");
                String version = data.getString("version", "0.0.0");

                System.out.println("Server Status: " + status);
                System.out.println("Server Version: " + version);

                // Server online, lanjut ke login screen
                // goToLoginScreen();
            }

            @Override
            public void onError(String error) {
                System.out.println("Server offline: " + error);
                // Tampilkan pesan server offline
                // showOfflineMessage();
            }
        });
    }
}

