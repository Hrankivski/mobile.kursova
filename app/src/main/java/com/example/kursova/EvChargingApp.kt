package com.example.kursova

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.kursova.core.connection.ConnectionState
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
import com.example.kursova.data.remote.RemoteConnectorDataSource
import com.example.kursova.data.remote.RemoteTariffDataSource
import com.example.kursova.data.remote.RemoteUserDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    lateinit var remoteUserDataSource: RemoteUserDataSource
    lateinit var remoteTariffDataSource: RemoteTariffDataSource
    lateinit var remoteConnectorDataSource: RemoteConnectorDataSource



    var currentUserId: Int? = null
    var currentUserIsAdmin: Boolean = false

    private val _connectionState = MutableStateFlow(ConnectionState.OFFLINE)
    val connectionState: StateFlow<ConnectionState> = _connectionState

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
            // Телефон підключається до ПК в локальній мереєі через 192.168.1.109
            // Порт сервера 8080
            .baseUrl("http://192.168.1.109:8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(EvChargingApiService::class.java)

        remoteChargingSessionDataSource = RemoteChargingSessionDataSource(apiService)
        remoteUserDataSource = RemoteUserDataSource(apiService)
        remoteTariffDataSource = RemoteTariffDataSource(apiService)
        remoteConnectorDataSource = RemoteConnectorDataSource(apiService)

        userCardRepository = UserCardRepositoryImpl(
            dao = database.userCardDao(),
            remote = remoteUserDataSource
        )
        tariffRepository = TariffRepositoryImpl(
            dao = database.tariffSettingsDao(),
            //remote = remoteTariffDataSource
        )
        connectorRepository = ConnectorRepositoryImpl(
            dao = database.connectorDao(),
            remote = remoteConnectorDataSource
        )
        chargingSessionRepository =
            ChargingSessionRepositoryImpl(
                dao = database.chargingSessionDao(),
                remote = remoteChargingSessionDataSource
            )
        currentUserId = null
    }

    fun markOnline() {
        _connectionState.value = ConnectionState.ONLINE
    }

    fun markOffline() {
        _connectionState.value = ConnectionState.OFFLINE
    }
}
