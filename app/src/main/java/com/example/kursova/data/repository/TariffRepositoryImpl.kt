package com.example.kursova.data.repository

import com.example.kursova.data.local.dao.TariffSettingsDao
import com.example.kursova.data.local.entity.TariffSettingsEntity
import com.example.kursova.domain.model.TariffSettings
import com.example.kursova.domain.repository.TariffRepository

class TariffRepositoryImpl(
    private val dao: TariffSettingsDao
) : TariffRepository {

    override suspend fun getSettings(): TariffSettings {
        val entity = dao.getSingle()
            ?: TariffSettingsEntity(
                dayPricePerKwh = 10.0,
                nightPricePerKwh = 7.0,
                nightStartHour = 23,
                nightEndHour = 7
            ).also { dao.insert(it) }

        return TariffSettings(
            id = entity.id,
            dayPricePerKwh = entity.dayPricePerKwh,
            nightPricePerKwh = entity.nightPricePerKwh,
            nightStartHour = entity.nightStartHour,
            nightEndHour = entity.nightEndHour
        )
    }

    override suspend fun saveSettings(settings: TariffSettings) {
        val entity = TariffSettingsEntity(
            id = settings.id,
            dayPricePerKwh = settings.dayPricePerKwh,
            nightPricePerKwh = settings.nightPricePerKwh,
            nightStartHour = settings.nightStartHour,
            nightEndHour = settings.nightEndHour
        )
        dao.insert(entity)
    }
}
