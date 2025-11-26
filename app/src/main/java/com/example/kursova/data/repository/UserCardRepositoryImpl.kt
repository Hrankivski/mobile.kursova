package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.UserCardDao
import com.example.kursova.data.local.entity.UserCardEntity
import com.example.kursova.domain.model.UserCard
import com.example.kursova.domain.repository.UserCardRepository

class UserCardRepositoryImpl(
    private val dao: UserCardDao
) : UserCardRepository {

    override suspend fun getByLogin(login: String): UserCard? =
        dao.getByLogin(login)?.toDomain()

    override suspend fun isLoginTaken(login: String): Boolean =
        dao.isLoginTaken(login)

    override suspend fun createUser(
        login: String,
        name: String,
        pinCode: String,
        isAdmin: Boolean
    ): Int {
        val entity = UserCardEntity(
            login = login,
            name = name,
            cardNumberMasked = "**** 0000",
            pinCode = pinCode,
            isAdmin = isAdmin
        )
        val id = dao.insert(entity)
        return id.toInt()
    }

    override suspend fun getAll(): List<UserCard> =
        dao.getAll().map { it.toDomain() }

    // 🔹 реалізація методу для use case AuthenticateUserByPinUseCase
    override suspend fun authenticateByPin(pin: String): UserCard? =
        dao.getByPin(pin)?.toDomain()

    private fun UserCardEntity.toDomain() = UserCard(
        id = id,
        login = login,
        name = name,
        cardNumberMasked = cardNumberMasked,
        pinCode = pinCode,
        isAdmin = isAdmin
    )
}
