package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.ConnectorDao
import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.model.ConnectorStatus
import com.example.kursova.domain.repository.ConnectorRepository

class ConnectorRepositoryImpl(
    private val connectorDao: ConnectorDao
) : ConnectorRepository {

    override suspend fun getAll(): List<Connector> =
        connectorDao.getAll().map {
            Connector(
                id = it.id,
                name = it.name,
                maxPowerKw = it.maxPowerKw,
                status = it.status
            )
        }

    override suspend fun getAvailable(): List<Connector> =
        getAll().filter { it.status == ConnectorStatus.AVAILABLE }

    override suspend fun getById(id: Int): Connector? =
        connectorDao.getById(id)?.let {
            Connector(
                id = it.id,
                name = it.name,
                maxPowerKw = it.maxPowerKw,
                status = it.status
            )
        }
}