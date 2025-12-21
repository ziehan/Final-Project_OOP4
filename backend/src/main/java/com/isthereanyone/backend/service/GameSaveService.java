package com.isthereanyone.backend.service;

import com.isthereanyone.backend.dto.GameSaveResponse;
import com.isthereanyone.backend.dto.SaveGameRequest;
import com.isthereanyone.backend.dto.SlotInfo;
import com.isthereanyone.backend.entity.GameSave;
import com.isthereanyone.backend.exception.InvalidOperationException;
import com.isthereanyone.backend.exception.ResourceNotFoundException;
import com.isthereanyone.backend.repository.GameSaveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer untuk game save operations
 */
@Service
@Transactional
public class GameSaveService {

    private final GameSaveRepository gameSaveRepository;

    public GameSaveService(GameSaveRepository gameSaveRepository) {
        this.gameSaveRepository = gameSaveRepository;
    }

    /**
     * Save game ke slot tertentu
     * Jika sudah ada, akan di-overwrite
     */
    public GameSaveResponse saveGame(SaveGameRequest request) {
        // Validasi slot ID (1-3)
        if (request.getSlotId() < 1 || request.getSlotId() > 3) {
            throw new InvalidOperationException("Slot ID harus antara 1 dan 3");
        }

        // Buat atau update save
        GameSave gameSave = gameSaveRepository
                .findByUserIdAndSlotId(request.getUserId(), request.getSlotId())
                .orElse(new GameSave());

        gameSave.setUserId(request.getUserId());
        gameSave.setSlotId(request.getSlotId());
        gameSave.setSaveData(request.getSaveData());

        GameSave saved = gameSaveRepository.save(gameSave);

        return toResponse(saved);
    }

    /**
     * Load game dari slot tertentu
     */
    @Transactional(readOnly = true)
    public GameSaveResponse loadGame(String userId, Integer slotId) {
        GameSave gameSave = gameSaveRepository
                .findByUserIdAndSlotId(userId, slotId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Game save", "userId/slotId", userId + "/" + slotId));

        return toResponse(gameSave);
    }

    /**
     * Ambil semua slots untuk user (termasuk yang kosong)
     * Mengembalikan info preview untuk setiap slot
     */
    @Transactional(readOnly = true)
    public List<SlotInfo> getAllSlots(String userId) {
        List<GameSave> saves = gameSaveRepository.findByUserId(userId);

        // Buat map dari save yang ada
        Map<Integer, GameSave> saveMap = new java.util.HashMap<>();
        for (GameSave save : saves) {
            saveMap.put(save.getSlotId(), save);
        }

        // Buat list 3 slot (1, 2, 3)
        List<SlotInfo> slots = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            if (saveMap.containsKey(i)) {
                GameSave save = saveMap.get(i);
                slots.add(SlotInfo.fromSaveData(i, save.getSaveData(), save.getLastUpdated()));
            } else {
                slots.add(SlotInfo.emptySlot(i));
            }
        }

        return slots;
    }

    /**
     * Hapus save di slot tertentu
     */
    public void deleteSlot(String userId, Integer slotId) {
        if (!gameSaveRepository.existsByUserIdAndSlotId(userId, slotId)) {
            throw new ResourceNotFoundException(
                    "Game save", "userId/slotId", userId + "/" + slotId);
        }

        gameSaveRepository.deleteByUserIdAndSlotId(userId, slotId);
    }

    /**
     * Hapus semua save milik user
     */
    public void deleteAllSlots(String userId) {
        gameSaveRepository.deleteByUserId(userId);
    }

    /**
     * Cek apakah slot tertentu sudah ada isinya
     */
    @Transactional(readOnly = true)
    public boolean isSlotExists(String userId, Integer slotId) {
        return gameSaveRepository.existsByUserIdAndSlotId(userId, slotId);
    }

    /**
     * Convert entity ke response DTO
     */
    private GameSaveResponse toResponse(GameSave gameSave) {
        return new GameSaveResponse(
                gameSave.getUserId(),
                gameSave.getSlotId(),
                gameSave.getSaveData(),
                gameSave.getLastUpdated()
        );
    }
}

