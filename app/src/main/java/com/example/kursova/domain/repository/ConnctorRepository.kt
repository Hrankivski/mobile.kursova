package com.example.kursova.domain.repository

import com.example.kursova.domain.model.Connector

interface ConnectorRepository {
    suspend fun getAll(): List<Connector>
    suspend fun getAvailable(): List<Connector>
    suspend fun getById(id: Int): Connector?
}