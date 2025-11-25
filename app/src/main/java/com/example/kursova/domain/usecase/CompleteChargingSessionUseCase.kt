package com.example.kursova.domain.usecase

import com.example.kursova.domain.repository.ChargingSessionRepository

class CompleteChargingSessionUseCase(
    private val sessionRepository: ChargingSessionRepository
) {
    suspend operator fun invoke(
        sessionId: Long,
        endTime: Long,
        energyKwh: Double,
        totalPrice: Double,
        tariffUsed: String
    ) = sessionRepository.completeSession(
        sessionId,
        endTime,
        energyKwh,
        totalPrice,
        tariffUsed
    )
}