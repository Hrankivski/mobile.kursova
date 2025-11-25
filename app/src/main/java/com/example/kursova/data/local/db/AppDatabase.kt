package com.example.kursova.data.local.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kursova.data.local.dao.*
import com.example.kursova.data.local.entity.*
import com.example.kursova.domain.model.ConnectorStatus

@Database(
    entities = [
        UserCardEntity::class,
        ConnectorEntity::class,
        TariffSettingsEntity::class,
        ChargingSessionEntity::class
    ],
    version = 1
)
@TypeConverters(ConnectorStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userCardDao(): UserCardDao
    abstract fun connectorDao(): ConnectorDao
    abstract fun tariffSettingsDao(): TariffSettingsDao
    abstract fun chargingSessionDao(): ChargingSessionDao

    companion object {
        val PrepopulateCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                // User card
                val userValues = ContentValues().apply {
                    put("id", 1)
                    put("name", "Demo User")
                    put("cardNumberMasked", "**** 1234")
                    put("pinCode", "1234")
                }
                db.insert("user_cards", SQLiteDatabase.CONFLICT_REPLACE, userValues)

                // Connectors
                insertConnector(db, 1, "Type 2", 22.0, ConnectorStatus.AVAILABLE)
                insertConnector(db, 2, "CCS", 50.0, ConnectorStatus.AVAILABLE)
                insertConnector(db, 3, "CHAdeMO", 50.0, ConnectorStatus.OUT_OF_ORDER)

                // Tariff
                val tariffValues = ContentValues().apply {
                    put("id", 1)
                    put("dayPricePerKwh", 10.0)
                    put("nightPricePerKwh", 7.0)
                    put("nightStartHour", 23)
                    put("nightEndHour", 7)
                }
                db.insert("tariff_settings", SQLiteDatabase.CONFLICT_REPLACE, tariffValues)
            }

            private fun insertConnector(
                db: SupportSQLiteDatabase,
                id: Int,
                name: String,
                power: Double,
                status: ConnectorStatus
            ) {
                val values = ContentValues().apply {
                    put("id", id)
                    put("name", name)
                    put("maxPowerKw", power)
                    put("status", status.name)
                }
                db.insert("connectors", SQLiteDatabase.CONFLICT_REPLACE, values)
            }
        }
    }
}
