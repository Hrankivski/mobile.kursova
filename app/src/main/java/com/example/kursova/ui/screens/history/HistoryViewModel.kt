package com.example.kursova.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HistoryItemUi(
    val id: Long,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val energyKwh: Double,
    val totalPrice: Double
)

data class HistoryUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val items: List<HistoryItemUi> = emptyList()
)

class HistoryViewModel : ViewModel() {

    private val sessionRepository = Graph.chargingSessionRepository

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                // починаємо завантаження
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val userId = Graph.currentUserId
                if (userId == null) {
                    _uiState.value = HistoryUiState(
                        isLoading = false,
                        error = "User not logged in"
                    )
                    return@launch
                }

                // тягнемо тільки сесії для поточного користувача
                val sessions = sessionRepository.getAllForUser(userId)

                val items = sessions.map { s ->
                    val durationSeconds =
                        ((s.endTime - s.startTime).coerceAtLeast(0L)) / 1000L

                    HistoryItemUi(
                        id = s.id,
                        startTimeMillis = s.startTime,
                        endTimeMillis = s.endTime,
                        durationSeconds = durationSeconds,
                        energyKwh = s.energyKwh,
                        totalPrice = s.totalPrice
                    )
                }

                _uiState.value = HistoryUiState(
                    isLoading = false,
                    items = items
                )
            } catch (e: Exception) {
                _uiState.value = HistoryUiState(
                    isLoading = false,
                    error = e.message ?: "Unexpected error"
                )
            }
        }
    }
}
