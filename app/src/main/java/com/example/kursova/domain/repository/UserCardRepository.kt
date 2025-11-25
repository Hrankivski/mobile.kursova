package com.example.kursova.domain.repository

import com.example.kursova.domain.model.UserCard

interface UserCardRepository {
    suspend fun authenticateByPin(pin: String): UserCard?
}