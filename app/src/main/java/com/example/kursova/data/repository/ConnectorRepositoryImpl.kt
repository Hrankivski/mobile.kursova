package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.ConnectorDao
import com.example.kursova.data.local.entity.ConnectorEntity
import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.model.ConnectorStatus
import com.example.kursova.domain.repository.ConnectorRepository

class ConnectorRepositoryImpl(
    private val dao: ConnectorDao
) : ConnectorRepository {

    override suspend fun getAll(): List<Connector> {
        // 1. Читаємо все з БД
        val existing = dao.getAll()

        // 2. Якщо щось є – просто мапимо й повертаємо
        if (existing.isNotEmpty()) {
            return existing.map { it.toDomain() }
        }

        // 3. Якщо таблиця порожня – один раз створюємо дефолти
        val defaults = listOf(
            ConnectorEntity(
                name = "CCS combo type2",
                maxPowerKw = 22.0,
                status = ConnectorStatus.AVAILABLE
            ),
            ConnectorEntity(
                name = "CHAdeMO",
                maxPowerKw = 11.0,
                status = ConnectorStatus.AVAILABLE
            ),
            ConnectorEntity(
                name = "Tesla",
                maxPowerKw = 50.0,
                status = ConnectorStatus.OUT_OF_ORDER
            )
        )

        dao.insertAll(defaults)
        return defaults.map { it.toDomain() }
    }

    override suspend fun getAvailable(): List<Connector> =
        getAll().filter { it.status == ConnectorStatus.AVAILABLE }

    override suspend fun getById(id: Int): Connector? =
        dao.getById(id)?.toDomain()

    override suspend fun updateStatus(id: Int, status: ConnectorStatus) {
        val entity = dao.getById(id) ?: return
        dao.update(entity.copy(status = status))
    }

    private fun ConnectorEntity.toDomain(): Connector =
        Connector(
            id = id,
            name = name,
            maxPowerKw = maxPowerKw,
            status = status
        )
}
