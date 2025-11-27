package com.example.kursova

import android.app.Application
import android.content.Context
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
import com.example.kursova.data.remote.EvChargingApiService
import com.example.kursova.data.remote.RemoteChargingSessionDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

    // --- Network ---
    lateinit var retrofit: retrofit2.Retrofit
    lateinit var apiService: com.example.kursova.data.remote.EvChargingApiService
    lateinit var remoteChargingSessionDataSource: com.example.kursova.data.remote.RemoteChargingSessionDataSource


    var currentUserId: Int? = null
    var currentUserIsAdmin: Boolean = false

    fun provide(appContext: Context) {
        // --- Room ---
        database = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "ev_charging.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // --- Network: OkHttp + Retrofit ---
        val logging = HttpLoggingInterceptor().apply {
            // Для дебагу – бачити запити/відповіді в Logcat
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            // Емулятор Android підключається до локального ПК через 10.0.2.2
            .baseUrl("http://10.0.2.2:8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(EvChargingApiService::class.java)
        remoteChargingSessionDataSource = RemoteChargingSessionDataSource(apiService)

        // --- Repositories (як у тебе було) ---
        userCardRepository = UserCardRepositoryImpl(database.userCardDao())
        connectorRepository = ConnectorRepositoryImpl(database.connectorDao())
        chargingSessionRepository =
            ChargingSessionRepositoryImpl(
                dao = database.chargingSessionDao(),
                remote = remoteChargingSessionDataSource
            )
        tariffRepository = TariffRepositoryImpl(database.tariffSettingsDao())

        currentUserId = null
    }

}
