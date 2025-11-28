package com.example.kursova.data.remote

import com.example.kursova.data.remote.dto.UserCardDto
import com.example.kursova.data.remote.dto.UserSyncResponseDto

class RemoteUserDataSource(
    private val api: EvChargingApiService
) {
    suspend fun syncUsers(users: List<UserCardDto>): UserSyncResponseDto =
        api.syncUsers(users)

    suspend fun getAllUsers(): List<UserCardDto> =
        api.getAllUsers()
}
