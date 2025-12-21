package com.isthereanyone.backend.repository;

import com.isthereanyone.backend.entity.GameSave;
import com.isthereanyone.backend.entity.GameSaveId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository untuk operasi database pada GameSave
 */
@Repository
public interface GameSaveRepository extends JpaRepository<GameSave, GameSaveId> {

    /**
     * Ambil semua save slots milik user tertentu
     */
    List<GameSave> findByUserId(String userId);

    /**
     * Ambil save tertentu berdasarkan userId dan slotId
     */
    Optional<GameSave> findByUserIdAndSlotId(String userId, Integer slotId);

    /**
     * Cek apakah save slot sudah ada
     */
    boolean existsByUserIdAndSlotId(String userId, Integer slotId);

    /**
     * Hapus save tertentu
     */
    void deleteByUserIdAndSlotId(String userId, Integer slotId);

    /**
     * Hapus semua save milik user
     */
    void deleteByUserId(String userId);

    /**
     * Hitung jumlah slot yang digunakan oleh user
     */
    @Query("SELECT COUNT(g) FROM GameSave g WHERE g.userId = :userId")
    long countByUserId(@Param("userId") String userId);
}

