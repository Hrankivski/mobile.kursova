package com.example.kursova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kursova.domain.model.ConnectorStatus

@Entity(tableName = "connectors")
data class ConnectorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val maxPowerKw: Double,
    val status: ConnectorStatus
)
