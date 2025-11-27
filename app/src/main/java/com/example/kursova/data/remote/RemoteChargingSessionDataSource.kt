package com.example.kursova.data.remote

import com.example.kursova.data.remote.dto.ChargingSessionSyncDto
import com.example.kursova.data.remote.dto.ChargingSessionSyncResponseDto

/**
 * Невеликий обгортач навколо EvChargingApiService.
 * Зручно для подальшого розширення (обробка помилок, ретраї тощо).
 */
class RemoteChargingSessionDataSource(
    private val api: EvChargingApiService
) {

    suspend fun syncSessions(
        sessions: List<ChargingSessionSyncDto>
    ): ChargingSessionSyncResponseDto {
        return api.syncSessions(sessions)
    }
}
