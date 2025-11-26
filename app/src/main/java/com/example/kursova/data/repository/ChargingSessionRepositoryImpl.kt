package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.ChargingSessionDao
import com.example.kursova.data.local.entity.ChargingSessionEntity
import com.example.kursova.domain.model.ChargingSession
import com.example.kursova.domain.repository.ChargingSessionRepository

class ChargingSessionRepositoryImpl(
    private val dao: ChargingSessionDao
) : ChargingSessionRepository {

    override suspend fun createSession(
        userCardId: Int,
        connectorId: Int,
        startTime: Long
    ): Long {
        val entity = ChargingSessionEntity(
            userCardId = userCardId,
            connectorId = connectorId,
            startTime = startTime,
            endTime = startTime,
            energyKwh = 0.0,
            totalPrice = 0.0,
            tariffUsed = "UNKNOWN"
        )
        return dao.insert(entity)
    }

    override suspend fun completeSession(
        sessionId: Long,
        endTime: Long,
        energyKwh: Double,
        totalPrice: Double,
        tariffUsed: String
    ) {
        val existing = dao.getById(sessionId) ?: return

        val updated = existing.copy(
            endTime = endTime,
            energyKwh = energyKwh,
            totalPrice = totalPrice,
            tariffUsed = tariffUsed
        )

        dao.insert(updated)
    }

    override suspend fun getAll(): List<ChargingSession> =
        dao.getAll().map { it.toDomain() }

    override suspend fun getById(id: Long): ChargingSession? =
        dao.getById(id)?.toDomain()

    private fun ChargingSessionEntity.toDomain(): ChargingSession =
        ChargingSession(
            id = id,
            userCardId = userCardId,
            connectorId = connectorId,
            startTime = startTime,
            endTime = endTime,
            energyKwh = energyKwh,
            totalPrice = totalPrice,
            tariffUsed = tariffUsed
        )
}
