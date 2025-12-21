package com.isthereanyone.backend.controller;

import com.isthereanyone.backend.dto.ApiResponse;
import com.isthereanyone.backend.dto.GameSaveResponse;
import com.isthereanyone.backend.dto.SaveGameRequest;
import com.isthereanyone.backend.dto.SlotInfo;
import com.isthereanyone.backend.service.GameSaveService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller untuk Game Save API
 *
 * Endpoints:
 * - POST   /api/save          → Save game ke slot
 * - GET    /api/save/{userId}/{slotId} → Load game dari slot
 * - GET    /api/save/{userId}/slots    → Get semua slots info
 * - DELETE /api/save/{userId}/{slotId} → Hapus slot tertentu
 * - DELETE /api/save/{userId}          → Hapus semua slots user
 */
@RestController
@RequestMapping("/api/save")
@CrossOrigin(origins = "*") // Allow CORS untuk game client
public class GameSaveController {

    private final GameSaveService gameSaveService;

    public GameSaveController(GameSaveService gameSaveService) {
        this.gameSaveService = gameSaveService;
    }

    /**
     * POST /api/save
     * Save game ke slot tertentu (create atau update)
     *
     * Request Body:
     * {
     *   "userId": "player123",
     *   "slotId": 1,
     *   "saveData": { ... game data ... }
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GameSaveResponse>> saveGame(
            @Valid @RequestBody SaveGameRequest request) {

        GameSaveResponse saved = gameSaveService.saveGame(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Game berhasil disimpan", saved));
    }

    /**
     * GET /api/save/{userId}/{slotId}
     * Load game dari slot tertentu
     */
    @GetMapping("/{userId}/{slotId}")
    public ResponseEntity<ApiResponse<GameSaveResponse>> loadGame(
            @PathVariable String userId,
            @PathVariable Integer slotId) {

        GameSaveResponse gameData = gameSaveService.loadGame(userId, slotId);
        return ResponseEntity.ok(ApiResponse.success("Game berhasil dimuat", gameData));
    }

    /**
     * GET /api/save/{userId}/slots
     * Get info semua slots untuk user (untuk tampilan di menu save/load)
     */
    @GetMapping("/{userId}/slots")
    public ResponseEntity<ApiResponse<List<SlotInfo>>> getAllSlots(
            @PathVariable String userId) {

        List<SlotInfo> slots = gameSaveService.getAllSlots(userId);
        return ResponseEntity.ok(ApiResponse.success("Slots berhasil dimuat", slots));
    }

    /**
     * DELETE /api/save/{userId}/{slotId}
     * Hapus save di slot tertentu
     */
    @DeleteMapping("/{userId}/{slotId}")
    public ResponseEntity<ApiResponse<Object>> deleteSlot(
            @PathVariable String userId,
            @PathVariable Integer slotId) {

        gameSaveService.deleteSlot(userId, slotId);
        return ResponseEntity.ok(ApiResponse.success("Slot berhasil dihapus", null));
    }

    /**
     * DELETE /api/save/{userId}
     * Hapus semua slots milik user
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> deleteAllSlots(
            @PathVariable String userId) {

        gameSaveService.deleteAllSlots(userId);
        return ResponseEntity.ok(ApiResponse.success("Semua slot berhasil dihapus", null));
    }

    /**
     * GET /api/save/{userId}/{slotId}/exists
     * Cek apakah slot sudah ada isinya
     */
    @GetMapping("/{userId}/{slotId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkSlotExists(
            @PathVariable String userId,
            @PathVariable Integer slotId) {

        boolean exists = gameSaveService.isSlotExists(userId, slotId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}

