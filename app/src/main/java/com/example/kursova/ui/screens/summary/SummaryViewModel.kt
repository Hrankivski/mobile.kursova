package com.example.kursova.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SummaryUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val connectorName: String = "",
    val powerKw: Double = 0.0,
    val startTimeMillis: Long = 0L,
    val endTimeMillis: Long = 0L,
    val durationSeconds: Long = 0L,
    val energyKwh: Double = 0.0,
    val totalPrice: Double = 0.0,
    val tariffUsed: String = ""
)

class SummaryViewModel(
    private val sessionId: Long
) : ViewModel() {

    private val sessionRepository = Graph.chargingSessionRepository
    private val connectorRepository = Graph.connectorRepository

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // 1. Тягнемо сесію
                val session = sessionRepository.getById(sessionId)
                if (session == null) {
                    _uiState.value = SummaryUiState(
                        isLoading = false,
                        error = "Session not found"
                    )
                    return@launch
                }

                // 2. Тягнемо конектор
                val connector = connectorRepository.getById(session.connectorId)

                // 3. Рахуємо тривалість
                val start = session.startTime
                val end = session.endTime
                val durationSeconds =
                    ((end - start).coerceAtLeast(0L)) / 1000L

                // 4. Оновлюємо стан
                _uiState.value = SummaryUiState(
                    isLoading = false,
                    connectorName = connector?.name ?: "Unknown",
                    powerKw = connector?.maxPowerKw ?: 0.0,
                    startTimeMillis = start,
                    endTimeMillis = end,
                    durationSeconds = durationSeconds,
                    energyKwh = session.energyKwh,
                    totalPrice = session.totalPrice,
                    tariffUsed = session.tariffUsed
                )
            } catch (e: Exception) {
                _uiState.value = SummaryUiState(
                    isLoading = false,
                    error = e.message ?: "Unexpected error"
                )
            }
        }
    }
}
