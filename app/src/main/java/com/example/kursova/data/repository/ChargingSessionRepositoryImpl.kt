package com.example.kursova.data.repository

import android.util.Log
import com.example.kursova.Graph
import com.example.kursova.data.local.dao.ChargingSessionDao
import com.example.kursova.data.local.entity.ChargingSessionEntity
import com.example.kursova.data.remote.RemoteChargingSessionDataSource
import com.example.kursova.data.remote.dto.ChargingSessionSyncDto
import com.example.kursova.domain.model.ChargingSession
import com.example.kursova.domain.repository.ChargingSessionRepository

class ChargingSessionRepositoryImpl(
    private val dao: ChargingSessionDao,
    private val remote: RemoteChargingSessionDataSource
) : ChargingSessionRepository {

    override suspend fun createSession(
        userCardId: Int,
        connectorId: Int,
        startTime: Long
    ): Long {
        // ВАЖЛИВО: використовуємо саме userCardId, який прийшов параметром
        // і НЕ чіпаємо тут Graph.currentUserId, щоб не було прихованих залежностей.
        val entity = ChargingSessionEntity(
            id = 0L,                    // Room сам поставить новий PK
            userCardId = userCardId,
            connectorId = connectorId,
            startTime = startTime,
            endTime = startTime,
            energyKwh = 0.0,
            totalPrice = 0.0,
            tariffUsed = "UNKNOWN",
            isSynced = false
        )

        Log.d(
            "ChargingSessionRepo",
            "createSession(): userCardId=$userCardId, connectorId=$connectorId, startTime=$startTime"
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
        val existing = dao.getById(sessionId)
        if (existing == null) {
            Log.w("ChargingSessionRepo", "completeSession(): session $sessionId not found")
            return
        }

        val updated = existing.copy(
            endTime = endTime,
            energyKwh = energyKwh,
            totalPrice = totalPrice,
            tariffUsed = tariffUsed,
            isSynced = false           // після змін позначаємо як не синхронізовану
        )

        Log.d(
            "ChargingSessionRepo",
            "completeSession(): sessionId=$sessionId, energy=$energyKwh, totalPrice=$totalPrice, tariff=$tariffUsed"
        )

        dao.insert(updated)
    }

    override suspend fun getAll(): List<ChargingSession> =
        dao.getAll().map { it.toDomain() }

    override suspend fun getAllForUser(userId: Int): List<ChargingSession> =
        dao.getAllForUser(userId).map { it.toDomain() }

    override suspend fun getById(id: Long): ChargingSession? =
        dao.getById(id)?.toDomain()

    /**
     * Синхронізація всіх несинхронізованих сесій на сервер.
     * Після успішної відповіді від сервера позначаємо їх як isSynced = true.
     */
    override suspend fun syncUnsyncedSessions() {
        val notSynced = dao.getNotSynced()
        if (notSynced.isEmpty()) {
            Log.d("SyncDebug", "No sessions to sync")
            return
        }

        Log.d("SyncDebug", "Trying to sync ${notSynced.size} sessions")

        val dtos = notSynced.map { entity ->
            ChargingSessionSyncDto(
                localId = entity.id,
                userId = entity.userCardId,
                connectorId = entity.connectorId,
                startTime = entity.startTime,
                endTime = entity.endTime,
                energyKwh = entity.energyKwh,
                totalPrice = entity.totalPrice,
                tariffUsed = entity.tariffUsed
            )
        }

        try {
            val response = remote.syncSessions(dtos)
            Log.d(
                "SyncDebug",
                "Server response: success=${response.success}, mapped=${response.mapped?.size ?: 0}"
            )

            if (!response.success || response.mapped.isNullOrEmpty()) {
                Graph.markOnline()
                return
            }

            val syncedLocalIds = response.mapped
                .mapNotNull { it.localId }
                .toSet()

            notSynced
                .filter { it.id in syncedLocalIds }
                .forEach { entity ->
                    dao.insert(entity.copy(isSynced = true))
                }

            Graph.markOnline()
            Log.d("SyncDebug", "Marked ${syncedLocalIds.size} sessions as synced")

        } catch (e: Exception) {
            Graph.markOffline()
            Log.e("SyncDebug", "Sync failed", e)
        }
    }

    // мапінг Room-entity -> доменна модель
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
