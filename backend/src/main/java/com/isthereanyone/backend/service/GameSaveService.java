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

@Service
@Transactional
public class GameSaveService {

    private final GameSaveRepository gameSaveRepository;

    public GameSaveService(GameSaveRepository gameSaveRepository) {
        this.gameSaveRepository = gameSaveRepository;
    }

    public GameSaveResponse saveGame(SaveGameRequest request) {
        if (request.getSlotId() < 1 || request.getSlotId() > 3) {
            throw new InvalidOperationException("Slot ID harus antara 1 dan 3");
        }

        GameSave gameSave = gameSaveRepository
                .findByUserIdAndSlotId(request.getUserId(), request.getSlotId())
                .orElse(new GameSave());

        gameSave.setUserId(request.getUserId());
        gameSave.setSlotId(request.getSlotId());
        gameSave.setSaveData(request.getSaveData());

        GameSave saved = gameSaveRepository.save(gameSave);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public GameSaveResponse loadGame(String userId, Integer slotId) {
        GameSave gameSave = gameSaveRepository
                .findByUserIdAndSlotId(userId, slotId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Game save", "userId/slotId", userId + "/" + slotId));
        return toResponse(gameSave);
    }

    @Transactional(readOnly = true)
    public List<SlotInfo> getAllSlots(String userId) {
        List<GameSave> saves = gameSaveRepository.findByUserId(userId);

        Map<Integer, GameSave> saveMap = new java.util.HashMap<>();
        for (GameSave save : saves) {
            saveMap.put(save.getSlotId(), save);
        }

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

    public void deleteSlot(String userId, Integer slotId) {
        if (!gameSaveRepository.existsByUserIdAndSlotId(userId, slotId)) {
            throw new ResourceNotFoundException(
                    "Game save", "userId/slotId", userId + "/" + slotId);
        }
        gameSaveRepository.deleteByUserIdAndSlotId(userId, slotId);
    }

    public void deleteAllSlots(String userId) {
        gameSaveRepository.deleteByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isSlotExists(String userId, Integer slotId) {
        return gameSaveRepository.existsByUserIdAndSlotId(userId, slotId);
    }

    private GameSaveResponse toResponse(GameSave gameSave) {
        return new GameSaveResponse(
                gameSave.getUserId(),
                gameSave.getSlotId(),
                gameSave.getSaveData(),
                gameSave.getLastUpdated()
        );
    }
}

