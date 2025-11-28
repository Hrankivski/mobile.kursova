package com.example.kursova.data.remote

import com.example.kursova.data.remote.dto.ConnectorDto
import com.example.kursova.data.remote.dto.SimpleResponseDto

class RemoteConnectorDataSource(
    private val api: EvChargingApiService
) {
    suspend fun syncConnectors(connectors: List<ConnectorDto>): SimpleResponseDto =
        api.syncConnectors(connectors)
}
