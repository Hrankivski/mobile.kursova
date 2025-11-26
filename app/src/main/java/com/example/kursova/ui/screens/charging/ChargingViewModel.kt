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

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRunning = true,
                    connectorName = connector?.name ?: "Unknown",
                    powerKw = powerKwInternal
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

            while (isTimerRunning) {
                delay(1000)
                elapsed += 1

                val energy = if (powerKwInternal > 0.0) {
                    powerKwInternal * (elapsed.toDouble() / 3600.0)
                } else 0.0

                val (pricePerKwh, tariffLabel) = calculateCurrentTariff()
                val totalPrice = energy * pricePerKwh

                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = elapsed,
                    energyKwh = energy,
                    totalPrice = totalPrice,
                    tariffLabel = tariffLabel,
                    isRunning = true
                )
            }
        }
    }

    private fun calculateCurrentTariff(): Pair<Double, String> {
        val tariffs = tariffSettings ?: return 0.0 to ""
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val isNight = isNightHour(
            hour,
            tariffs.nightStartHour,
            tariffs.nightEndHour
        )

        return if (isNight) {
            tariffs.nightPricePerKwh to "NIGHT"
        } else {
            tariffs.dayPricePerKwh to "DAY"
        }
    }

    private fun isNightHour(hour: Int, nightStart: Int, nightEnd: Int): Boolean {
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
                sessionRepository.completeSession(
                    sessionId = sessionId,
                    endTime = endTime,
                    energyKwh = finalState.energyKwh,
                    totalPrice = finalState.totalPrice,
                    tariffUsed = tariffLabel
                )

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
