package com.example.kursova.domain.model

data class Connector(
    val id: Int,
    val name: String,
    val maxPowerKw: Double,
    val status: ConnectorStatus
)
