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

@RestController
@RequestMapping("/api/save")
@CrossOrigin(origins = "*")
public class GameSaveController {

    private final GameSaveService gameSaveService;

    public GameSaveController(GameSaveService gameSaveService) {
        this.gameSaveService = gameSaveService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GameSaveResponse>> saveGame(
            @Valid @RequestBody SaveGameRequest request) {
        GameSaveResponse saved = gameSaveService.saveGame(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Game berhasil disimpan", saved));
    }

    @GetMapping("/{userId}/{slotId}")
    public ResponseEntity<ApiResponse<GameSaveResponse>> loadGame(
            @PathVariable String userId,
            @PathVariable Integer slotId) {
        GameSaveResponse gameData = gameSaveService.loadGame(userId, slotId);
        return ResponseEntity.ok(ApiResponse.success("Game berhasil dimuat", gameData));
    }

    @GetMapping("/{userId}/slots")
    public ResponseEntity<ApiResponse<List<SlotInfo>>> getAllSlots(@PathVariable String userId) {
        List<SlotInfo> slots = gameSaveService.getAllSlots(userId);
        return ResponseEntity.ok(ApiResponse.success("Slots berhasil dimuat", slots));
    }

    @DeleteMapping("/{userId}/{slotId}")
    public ResponseEntity<ApiResponse<Object>> deleteSlot(
            @PathVariable String userId,
            @PathVariable Integer slotId) {
        gameSaveService.deleteSlot(userId, slotId);
        return ResponseEntity.ok(ApiResponse.success("Slot berhasil dihapus", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> deleteAllSlots(@PathVariable String userId) {
        gameSaveService.deleteAllSlots(userId);
        return ResponseEntity.ok(ApiResponse.success("Semua slot berhasil dihapus", null));
    }

    @GetMapping("/{userId}/{slotId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkSlotExists(
            @PathVariable String userId,
            @PathVariable Integer slotId) {

        boolean exists = gameSaveService.isSlotExists(userId, slotId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}

