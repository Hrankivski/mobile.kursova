package com.example.kursova.data.local.db

import androidx.room.TypeConverter
import com.example.kursova.domain.model.ConnectorStatus

class ConnectorStatusConverter {

    @TypeConverter
    fun fromString(value: String?): ConnectorStatus? =
        value?.let { ConnectorStatus.valueOf(it) }

    @TypeConverter
    fun toString(status: ConnectorStatus?): String? =
        status?.name
}
