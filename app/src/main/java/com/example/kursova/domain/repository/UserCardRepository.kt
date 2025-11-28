package com.example.kursova.domain.repository

import com.example.kursova.domain.model.UserCard

interface UserCardRepository {

    suspend fun signUp(
        login: String,
        pinCode: String,
        name: String
    ): Int

    suspend fun authenticate(
        login: String,
        pinCode: String
    ): UserCard?

    suspend fun authenticateByPin(pinCode: String): UserCard?

    suspend fun getAllLocal(): List<UserCard>

    suspend fun syncUsersUp()

    suspend fun syncUsersDown()
}
