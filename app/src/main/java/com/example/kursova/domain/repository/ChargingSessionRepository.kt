package com.example.kursova.domain.repository

import com.example.kursova.domain.model.ChargingSession

interface ChargingSessionRepository {

    suspend fun createSession(
        userCardId: Int,
        connectorId: Int,
        startTime: Long
    ): Long

    suspend fun completeSession(
        sessionId: Long,
        endTime: Long,
        energyKwh: Double,
        totalPrice: Double,
        tariffUsed: String
    )

    suspend fun getAll(): List<ChargingSession>

    suspend fun getById(id: Long): ChargingSession?

    suspend fun getAllForUser(userId: Int): List<ChargingSession>
}