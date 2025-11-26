package com.example.kursova.domain.repository

import com.example.kursova.domain.model.UserCard

interface UserCardRepository {

    suspend fun getByLogin(login: String): UserCard?

    suspend fun isLoginTaken(login: String): Boolean

    suspend fun createUser(
        login: String,
        name: String,
        pinCode: String,
        isAdmin: Boolean = false
    ): Int

    suspend fun getAll(): List<UserCard>

    suspend fun authenticateByPin(pin: String): UserCard?
}
