package com.example.kursova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charging_sessions")
data class ChargingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userCardId: Int,
    val connectorId: Int,
    val startTime: Long,
    val endTime: Long,
    val energyKwh: Double,
    val totalPrice: Double,
    val tariffUsed: String,
    val syncStatus: String = "LOCAL_ONLY",
    val remoteId: Long? = null
)
