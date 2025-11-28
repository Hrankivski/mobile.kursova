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

        return entity.toDomain()
    }

    override suspend fun updateSettings(settings: TariffSettings) {
        val entity = TariffSettingsEntity(
            id = settings.id,
            dayPricePerKwh = settings.dayPricePerKwh,
            nightPricePerKwh = settings.nightPricePerKwh,
            nightStartHour = settings.nightStartHour,
            nightEndHour = settings.nightEndHour
        )
        dao.insert(entity)
        // якщо пізніше додамо серверну синхронізацію тарифів —
        // тут можна буде викликати remote.syncTariffs(entity)
    }

    /**
     * Тимчасова реалізація refreshFromServer.
     * Зараз тарифи живуть у локальній БД і ми просто гарантуємо, що
     * запис існує (через getSettings()).
     *
     * Якщо ти пізніше зробиш API на сервері для тарифів:
     * - додаємо сюди remote-джерело в конструктор
     * - тягнемо DTO з сервера
     * - зберігаємо його в dao.insert(...)
     */
    override suspend fun refreshFromServer() {
        // локальний варіант: нічого не робимо або просто
        // гарантуємо, що є дефолтні налаштування
        getSettings()
    }

    private fun TariffSettingsEntity.toDomain(): TariffSettings =
        TariffSettings(
            id = id,
            dayPricePerKwh = dayPricePerKwh,
            nightPricePerKwh = nightPricePerKwh,
            nightStartHour = nightStartHour,
            nightEndHour = nightEndHour
        )
}
