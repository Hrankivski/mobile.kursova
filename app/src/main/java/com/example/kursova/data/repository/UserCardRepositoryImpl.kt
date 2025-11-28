package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.UserCardDao
import com.example.kursova.data.local.entity.UserCardEntity
import com.example.kursova.data.remote.RemoteUserDataSource
import com.example.kursova.data.remote.dto.UserCardDto
import com.example.kursova.domain.model.UserCard
import com.example.kursova.domain.repository.UserCardRepository

class UserCardRepositoryImpl(
    private val dao: UserCardDao,
    private val remote: RemoteUserDataSource
) : UserCardRepository {

    override suspend fun signUp(
        login: String,
        pinCode: String,
        name: String
    ): Int {
        val entity = UserCardEntity(
            login = login,
            name = name,
            cardNumberMasked = "**** 0000",
            pinCode = pinCode,
            isAdmin = false,
            isSynced = false
        )
        val id = dao.insert(entity).toInt()
        return id
    }

    override suspend fun authenticate(
        login: String,
        pinCode: String
    ): UserCard? {
        val entity = dao.getByLogin(login) ?: return null
        return if (entity.pinCode == pinCode) {
            entity.toDomain()
        } else {
            null
        }
    }

    override suspend fun authenticateByPin(pinCode: String): UserCard? {
        val entity = dao.getByPin(pinCode) ?: return null
        return entity.toDomain()
    }

    override suspend fun getAllLocal(): List<UserCard> =
        dao.getAll().map { it.toDomain() }

    override suspend fun syncUsersUp() {
        val notSynced = dao.getNotSynced()
        if (notSynced.isEmpty()) return

        val dtos = notSynced.map { e ->
            UserCardDto(
                localId = e.id,
                login = e.login,
                name = e.name,
                cardNumberMasked = e.cardNumberMasked,
                pinCode = e.pinCode,
                isAdmin = e.isAdmin
            )
        }

        val response = remote.syncUsers(dtos)
        if (!response.success || response.mapped.isNullOrEmpty()) return

        val syncedLocalIds = response.mapped
            .mapNotNull { it.localId }
            .toSet()

        val updated = notSynced.map { e ->
            if (e.id in syncedLocalIds) {
                e.copy(isSynced = true)
            } else {
                e
            }
        }

        dao.insertAll(updated)
    }

    override suspend fun syncUsersDown() {
        val remoteUsers = remote.getAllUsers()
        if (remoteUsers.isEmpty()) return

        val existing = dao.getAll()
        val existingByLogin = existing.associateBy { it.login }

        val entities = remoteUsers.map { dto ->
            val local = existingByLogin[dto.login]
            UserCardEntity(
                id = local?.id ?: 0,
                login = dto.login,
                name = dto.name,
                cardNumberMasked = dto.cardNumberMasked,
                pinCode = dto.pinCode,
                isAdmin = dto.isAdmin,
                isSynced = true
            )
        }

        dao.insertAll(entities)
    }

    private fun UserCardEntity.toDomain(): UserCard =
        UserCard(
            id = id,
            login = login,
            name = name,
            cardNumberMasked = cardNumberMasked,
            isAdmin = isAdmin
        )
}
