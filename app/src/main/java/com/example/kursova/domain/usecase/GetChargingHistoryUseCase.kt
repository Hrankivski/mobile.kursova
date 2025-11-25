package com.example.kursova.domain.usecase

import com.example.kursova.domain.model.ChargingSession
import com.example.kursova.domain.repository.ChargingSessionRepository

class GetChargingHistoryUseCase(
    private val sessionRepository: ChargingSessionRepository
) {
    suspend operator fun invoke(): List<ChargingSession> =
        sessionRepository.getAll()
}