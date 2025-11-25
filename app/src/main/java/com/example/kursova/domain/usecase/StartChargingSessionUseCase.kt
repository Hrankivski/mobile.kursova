package com.example.kursova.domain.usecase

import com.example.kursova.domain.repository.ChargingSessionRepository

class StartChargingSessionUseCase(
    private val sessionRepository: ChargingSessionRepository
) {
    suspend operator fun invoke(
        userCardId: Int,
        connectorId: Int,
        startTime: Long
    ): Long = sessionRepository.createSession(userCardId, connectorId, startTime)
}