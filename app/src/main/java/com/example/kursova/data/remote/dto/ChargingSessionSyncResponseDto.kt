package com.example.kursova.data.remote.dto

/**
 * Відповідь сервера на /api/mobile/sessions/sync.
 * success = true/false
 * mapped = список зіставлень локальних id з remoteId (id у MySQL).
 */
data class ChargingSessionSyncResponseDto(
    val success: Boolean,
    val mapped: List<SessionIdMappingDto>?
)

data class SessionIdMappingDto(
    val localId: Long?,
    val remoteId: Long?
)
