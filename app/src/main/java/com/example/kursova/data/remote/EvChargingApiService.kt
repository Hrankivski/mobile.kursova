package com.example.kursova.data.remote

import com.example.kursova.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface EvChargingApiService {

    @POST("api/mobile/sessions/sync")
    suspend fun syncSessions(
        @Body sessions: List<ChargingSessionSyncDto>
    ): ChargingSessionSyncResponseDto

    @POST("api/mobile/users/sync")
    suspend fun syncUsers(
        @Body users: List<UserCardDto>
    ): UserSyncResponseDto

    @GET("api/mobile/users")
    suspend fun getAllUsers(): List<UserCardDto>

    @GET("api/mobile/tariff")
    suspend fun getTariff(): TariffDto

    @POST("api/mobile/connectors/sync")
    suspend fun syncConnectors(
        @Body connectors: List<ConnectorDto>
    ): SimpleResponseDto
}

