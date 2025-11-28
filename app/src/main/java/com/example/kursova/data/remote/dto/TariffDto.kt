package com.example.kursova.data.remote.dto

data class TariffDto(
    val id: Int,
    val dayPricePerKwh: Double,
    val nightPricePerKwh: Double,
    val nightStartHour: Int,
    val nightEndHour: Int
)
