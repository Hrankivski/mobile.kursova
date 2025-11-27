package com.example.kursova.data.remote

import com.example.kursova.data.remote.dto.ChargingSessionSyncDto
import com.example.kursova.data.remote.dto.ChargingSessionSyncResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface EvChargingApiService {

    /**
     * Синхронізація сесій зарядки з мобільного додатку на сервер.
     * Відправляємо масив сесій: [ { ... }, { ... } ]
     */
    @POST("api/mobile/sessions/sync")
    suspend fun syncSessions(
        @Body sessions: List<ChargingSessionSyncDto>
    ): ChargingSessionSyncResponseDto
}
