package com.example.kursova.ui.screens.charging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.TariffSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class ChargingUiState(
    val isLoading: Boolean = true,
    val isRunning: Boolean = false,
    val isCompleting: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null,
    val connectorName: String = "",
    val powerKw: Double = 0.0,
    val elapsedSeconds: Long = 0L,
    val energyKwh: Double = 0.0,
    val totalPrice: Double = 0.0,
    val tariffLabel: String = ""
)

class ChargingViewModel(
    private val sessionId: Long
) : ViewModel() {

    private val sessionRepository = Graph.chargingSessionRepository
    private val connectorRepository = Graph.connectorRepository
    private val tariffRepository = Graph.tariffRepository

    private val _uiState = MutableStateFlow(ChargingUiState())
    val uiState: StateFlow<ChargingUiState> = _uiState

    private var isTimerRunning: Boolean = false
    private var powerKwInternal: Double = 0.0
    private var startTimeMillis: Long = 0L
    private var tariffSettings: TariffSettings? = null

    // Фіксуємо тариф і ціну на момент старту
    private var pricePerKwhInternal: Double = 0.0
    private var tariffLabelInternal: String = ""

    init {
        loadInitialDataAndStartTimer()
    }

    private fun loadInitialDataAndStartTimer() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val session = sessionRepository.getById(sessionId)
                if (session == null) {
                    _uiState.value = ChargingUiState(
                        isLoading = false,
                        error = "Session not found"
                    )
                    return@launch
                }

                startTimeMillis = session.startTime

                val connector = connectorRepository.getById(session.connectorId)
                powerKwInternal = connector?.maxPowerKw ?: 0.0

                val tariffs = tariffRepository.getSettings()
                tariffSettings = tariffs

                // Визначаємо годину старту сесії
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = startTimeMillis
                }
                val hour = calendar.get(Calendar.HOUR_OF_DAY)

                // Перевіряємо час для вибору тарифу
                val isNight = isNightHour(
                    hour,
                    tariffs.nightStartHour,
                    tariffs.nightEndHour
                )

                // Вибираємо тариф
                if (isNight) {
                    pricePerKwhInternal = tariffs.nightPricePerKwh
                    tariffLabelInternal = "NIGHT"
                } else {
                    pricePerKwhInternal = tariffs.dayPricePerKwh
                    tariffLabelInternal = "DAY"
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRunning = true,
                    connectorName = connector?.name ?: "Unknown",
                    powerKw = powerKwInternal,
                    tariffLabel = tariffLabelInternal,
                    elapsedSeconds = 0L,
                    energyKwh = 0.0,
                    totalPrice = 0.0
                )

                startTimer()
            } catch (e: Exception) {
                _uiState.value = ChargingUiState(
                    isLoading = false,
                    error = e.message ?: "Unexpected error"
                )
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            isTimerRunning = true
            var elapsed = 0L

            // Ставимо посекундний таймер
            while (isTimerRunning) {
                delay(1000)
                elapsed += 1

                // Нараховуємо споживання енергії
                val energy = if (powerKwInternal > 0.0) {
                    powerKwInternal * (elapsed.toDouble() / 3600.0)
                } else 0.0

                val totalPrice = energy * pricePerKwhInternal

                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = elapsed,
                    energyKwh = energy,
                    totalPrice = totalPrice,
                    tariffLabel = tariffLabelInternal,
                    isRunning = true
                )
            }
        }
    }

    private fun isNightHour(hour: Int, nightStart: Int, nightEnd: Int): Boolean {
        // ніч може бути в межах одного дня (22–6) або "через північ"
        return if (nightStart <= nightEnd) {
            hour in nightStart until nightEnd
        } else {
            hour >= nightStart || hour < nightEnd
        }
    }

    fun onFinishCharging() {
        if (!_uiState.value.isRunning || isTimerRunning.not()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCompleting = true)
            isTimerRunning = false

            val finalState = _uiState.value
            val endTime = System.currentTimeMillis()
            val tariffLabel = if (finalState.tariffLabel.isBlank()) "DAY" else finalState.tariffLabel

            try {
                // 1. оновлюємо сесію в локальній БД
                sessionRepository.completeSession(
                    sessionId = sessionId,
                    endTime = endTime,
                    energyKwh = finalState.energyKwh,
                    totalPrice = finalState.totalPrice,
                    tariffUsed = tariffLabel
                )

                // 2. пробуємо синхронізувати всі несинхронізовані сесії з сервером
                try {
                    sessionRepository.syncUnsyncedSessions()
                } catch (e: Exception) {
                    // якщо немає мережі або сервер не відповів – не кидаємо помилку, щоб не зупиняти додаток
                }

                _uiState.value = finalState.copy(
                    isCompleting = false,
                    isRunning = false,
                    isCompleted = true
                )
            } catch (e: Exception) {
                _uiState.value = finalState.copy(
                    isCompleting = false,
                    error = e.message ?: "Failed to complete session"
                )
            }
        }
    }

}
