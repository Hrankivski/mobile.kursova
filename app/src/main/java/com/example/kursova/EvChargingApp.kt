package com.example.kursova

import android.app.Application
import androidx.room.Room
import com.example.kursova.data.local.db.AppDatabase
import com.example.kursova.data.repository.ChargingSessionRepositoryImpl
import com.example.kursova.data.repository.ConnectorRepositoryImpl
import com.example.kursova.data.repository.TariffRepositoryImpl
import com.example.kursova.data.repository.UserCardRepositoryImpl
import com.example.kursova.domain.repository.ChargingSessionRepository
import com.example.kursova.domain.repository.ConnectorRepository
import com.example.kursova.domain.repository.TariffRepository
import com.example.kursova.domain.repository.UserCardRepository

class EvChargingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}

object Graph {
    lateinit var database: AppDatabase
        private set

    lateinit var userCardRepository: UserCardRepository
        private set

    lateinit var connectorRepository: ConnectorRepository
        private set

    lateinit var tariffRepository: TariffRepository
        private set

    lateinit var chargingSessionRepository: ChargingSessionRepository
        private set

    var currentUserId: Int? = null
    var currentUserIsAdmin: Boolean = false

    fun provide(app: Application) {
        database = Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "ev_charging_db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(AppDatabase.PrepopulateCallback)
            .build()

        userCardRepository = UserCardRepositoryImpl(database.userCardDao())
        connectorRepository = ConnectorRepositoryImpl(database.connectorDao())
        tariffRepository = TariffRepositoryImpl(database.tariffSettingsDao())
        chargingSessionRepository =
            ChargingSessionRepositoryImpl(database.chargingSessionDao())
    }
}
