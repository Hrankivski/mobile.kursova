package com.example.kursova.domain.repository

import com.example.kursova.domain.model.TariffSettings

interface TariffRepository {
    suspend fun getSettings(): TariffSettings
}