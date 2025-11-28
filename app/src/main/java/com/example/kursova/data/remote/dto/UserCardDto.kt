package com.example.kursova.data.remote.dto

data class UserCardDto(
    val localId: Int? = null,
    val login: String,
    val name: String,
    val cardNumberMasked: String,
    val pinCode: String,
    val isAdmin: Boolean
)

data class UserSyncResponseDto(
    val success: Boolean,
    val mapped: List<UserIdMappingDto>?
)

data class UserIdMappingDto(
    val localId: Int?,
    val remoteId: Int?
)
