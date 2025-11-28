package com.example.kursova.domain.repository

import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.model.ConnectorStatus

interface ConnectorRepository {
    suspend fun getAll(): List<Connector>
    suspend fun getAvailable(): List<Connector>
    suspend fun getById(id: Int): Connector?

    suspend fun updateStatus(id: Int, status: ConnectorStatus)

    suspend fun syncConnectorsToServer()
}
