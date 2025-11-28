package com.example.kursova.data.remote

import com.example.kursova.data.remote.dto.TariffDto

class RemoteTariffDataSource(
    private val api: EvChargingApiService
) {
    suspend fun getTariff(): TariffDto = api.getTariff()
}
