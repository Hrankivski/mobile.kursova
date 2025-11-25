package com.example.kursova.domain.usecase

import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.repository.ConnectorRepository

class GetAvailableConnectorsUseCase(
    private val connectorRepository: ConnectorRepository
) {
    suspend operator fun invoke(): List<Connector> =
        connectorRepository.getAvailable()
}