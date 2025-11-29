package com.example.kursova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kursova.data.local.entity.ConnectorEntity

@Dao
interface ConnectorDao {
    @Query("SELECT * FROM connectors")
    suspend fun getAll(): List<ConnectorEntity>

    @Query("SELECT * FROM connectors WHERE id = :id")
    suspend fun getById(id: Int): ConnectorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(connectors: List<ConnectorEntity>)

    @Update
    suspend fun update(connector: ConnectorEntity)
}
