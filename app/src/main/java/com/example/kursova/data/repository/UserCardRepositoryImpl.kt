package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.UserCardDao
import com.example.kursova.domain.model.UserCard
import com.example.kursova.domain.repository.UserCardRepository

class UserCardRepositoryImpl(
    private val userCardDao: UserCardDao
) : UserCardRepository {

    override suspend fun authenticateByPin(pin: String): UserCard? {
        val entity = userCardDao.getByPin(pin) ?: return null
        return UserCard(
            id = entity.id,
            name = entity.name,
            cardNumberMasked = entity.cardNumberMasked
        )
    }
}