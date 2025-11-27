package com.example.kursova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursova.data.local.entity.ChargingSessionEntity

@Dao
interface ChargingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChargingSessionEntity): Long

    @Query("SELECT * FROM charging_sessions ORDER BY startTime DESC")
    suspend fun getAll(): List<ChargingSessionEntity>

    @Query("SELECT * FROM charging_sessions WHERE id = :id")
    suspend fun getById(id: Long): ChargingSessionEntity?

    @Query("SELECT * FROM charging_sessions WHERE isSynced = 0")
    suspend fun getNotSynced(): List<ChargingSessionEntity>

    // 🔹 новий метод – усі сесії для конкретного користувача
    @Query("SELECT * FROM charging_sessions WHERE userCardId = :userId ORDER BY startTime DESC")
    suspend fun getAllForUser(userId: Int): List<ChargingSessionEntity>
}
