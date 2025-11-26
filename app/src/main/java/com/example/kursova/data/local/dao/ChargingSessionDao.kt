package com.example.kursova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursova.data.local.entity.ChargingSessionEntity

@Dao
interface ChargingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ChargingSessionEntity): Long

    @Query("SELECT * FROM charging_sessions ORDER BY startTime DESC")
    suspend fun getAll(): List<ChargingSessionEntity>

    @Query("SELECT * FROM charging_sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ChargingSessionEntity?
}
