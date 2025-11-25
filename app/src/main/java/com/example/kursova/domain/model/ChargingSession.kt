package com.example.kursova.domain.model

data class ChargingSession(
    val id: Long,
    val userCardId: Int,
    val connectorId: Int,
    val startTime: Long,
    val endTime: Long,
    val energyKwh: Double,
    val totalPrice: Double,
    val tariffUsed: String
)
