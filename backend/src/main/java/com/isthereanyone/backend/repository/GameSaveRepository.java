package com.isthereanyone.backend.repository;

import com.isthereanyone.backend.entity.GameSave;
import com.isthereanyone.backend.entity.GameSaveId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameSaveRepository extends JpaRepository<GameSave, GameSaveId> {
    List<GameSave> findByUserId(String userId);
    Optional<GameSave> findByUserIdAndSlotId(String userId, Integer slotId);
    boolean existsByUserIdAndSlotId(String userId, Integer slotId);
    void deleteByUserIdAndSlotId(String userId, Integer slotId);
    void deleteByUserId(String userId);

    @Query("SELECT COUNT(g) FROM GameSave g WHERE g.userId = :userId")
    long countByUserId(@Param("userId") String userId);
}

