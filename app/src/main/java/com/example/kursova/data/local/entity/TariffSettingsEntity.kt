package com.example.kursova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tariff_settings")
data class TariffSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val dayPricePerKwh: Double,
    val nightPricePerKwh: Double,
    val nightStartHour: Int,
    val nightEndHour: Int
)
