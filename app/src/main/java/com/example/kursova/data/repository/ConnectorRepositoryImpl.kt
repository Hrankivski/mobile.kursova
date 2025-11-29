package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.ConnectorDao
import com.example.kursova.data.local.entity.ConnectorEntity
import com.example.kursova.data.remote.RemoteConnectorDataSource
import com.example.kursova.data.remote.dto.ConnectorDto
import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.model.ConnectorStatus
import com.example.kursova.domain.repository.ConnectorRepository

class ConnectorRepositoryImpl(
    private val dao: ConnectorDao,
    private val remote: RemoteConnectorDataSource
) : ConnectorRepository {

    /**
     * Якщо в таблиці немає жодного конектора – створюємо кілька дефолтних.
     */
    private suspend fun ensureSeeded(): List<ConnectorEntity> {
        val existing = dao.getAll()
        if (existing.isNotEmpty()) return existing

        val defaults = listOf(
            ConnectorEntity(
                name = "CCS-1 50 kW",
                maxPowerKw = 50.0,
                status = ConnectorStatus.AVAILABLE
            ),
            ConnectorEntity(
                name = "CCS-1 150 kW",
                maxPowerKw = 150.0,
                status = ConnectorStatus.AVAILABLE
            ),
            ConnectorEntity(
                name = "Type 2 22 kW",
                maxPowerKw = 22.0,
                status = ConnectorStatus.AVAILABLE
            )
        )

        dao.insertAll(defaults)
        return dao.getAll()
    }

    override suspend fun getAll(): List<Connector> =
        ensureSeeded().map { it.toDomain() }

    override suspend fun getAvailable(): List<Connector> =
        ensureSeeded()
            .filter { it.status == ConnectorStatus.AVAILABLE }
            .map { it.toDomain() }

    override suspend fun getById(id: Int): Connector? =
        ensureSeeded()
            .firstOrNull { it.id == id }
            ?.toDomain()

    override suspend fun updateStatus(id: Int, status: ConnectorStatus) {
        val entity = dao.getById(id) ?: return
        dao.update(entity.copy(status = status))
    }

    override suspend fun syncConnectorsToServer() {
        val local = dao.getAll()
        if (local.isEmpty()) return

        val dtos = local.map { e ->
            ConnectorDto(
                id = e.id,
                name = e.name,
                maxPowerKw = e.maxPowerKw,
                status = e.status.name
            )
        }

        remote.syncConnectors(dtos)
        // Сервер тут просто дзеркало, локально нічого не змінюємо
    }

    private fun ConnectorEntity.toDomain(): Connector =
        Connector(
            id = id,
            name = name,
            maxPowerKw = maxPowerKw,
            status = status
        )
}
