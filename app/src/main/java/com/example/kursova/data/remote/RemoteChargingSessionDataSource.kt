package com.example.kursova.data.remote

import com.example.kursova.data.remote.dto.ChargingSessionSyncDto
import com.example.kursova.data.remote.dto.ChargingSessionSyncResponseDto

class RemoteChargingSessionDataSource(
    private val api: EvChargingApiService
) {

    suspend fun syncSessions(
        sessions: List<ChargingSessionSyncDto>
    ): ChargingSessionSyncResponseDto {
        return api.syncSessions(sessions)
    }
}
