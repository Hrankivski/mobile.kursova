package com.example.kursova.domain.model

data class TariffSettings(
    val id: Int = 1,
    val dayPricePerKwh: Double,
    val nightPricePerKwh: Double,
    val nightStartHour: Int,
    val nightEndHour: Int
)
