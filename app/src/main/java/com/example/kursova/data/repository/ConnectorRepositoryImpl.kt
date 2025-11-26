package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.ConnectorDao
import com.example.kursova.data.local.entity.ConnectorEntity
import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.model.ConnectorStatus
import com.example.kursova.domain.repository.ConnectorRepository

class ConnectorRepositoryImpl(
    private val dao: ConnectorDao
) : ConnectorRepository {

    override suspend fun getAll(): List<Connector> =
        dao.getAll().map { it.toDomain() }

    override suspend fun getAvailable(): List<Connector> =
        dao.getAll()
            .filter { it.status == ConnectorStatus.AVAILABLE }
            .map { it.toDomain() }

    override suspend fun getById(id: Int): Connector? =
        dao.getById(id)?.toDomain()

    override suspend fun updateStatus(id: Int, status: ConnectorStatus) {
        val existing = dao.getById(id) ?: return
        val updated = existing.copy(status = status)
        dao.update(updated)
    }

    private fun ConnectorEntity.toDomain(): Connector =
        Connector(
            id = id,
            name = name,
            maxPowerKw = maxPowerKw,
            status = status
        )
}
