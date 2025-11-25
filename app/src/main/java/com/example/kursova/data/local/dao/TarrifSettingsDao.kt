package com.example.kursova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursova.data.local.entity.TariffSettingsEntity

@Dao
interface TariffSettingsDao {

    @Query("SELECT * FROM tariff_settings WHERE id = 1")
    suspend fun getSingle(): TariffSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: TariffSettingsEntity)
}
