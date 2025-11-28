package com.example.kursova.data.remote.dto

/**
 * DTO для відправки сесій на сервер.
 * Один обʼєкт = одна локальна сесія.
 */
data class ChargingSessionSyncDto(
    val localId: Long?,    // id із локальної Room-таблиці (для зіставлення)
    val userId: Int,
    val connectorId: Int,
    val startTime: Long,
    val endTime: Long,
    val energyKwh: Double,
    val totalPrice: Double,
    val tariffUsed: String
)
