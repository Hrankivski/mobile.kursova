package com.example.kursova.ui.screens.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminLogItemUi(
    val id: Long,
    val userLogin: String,
    val userName: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val energyKwh: Double,
    val totalPrice: Double
)

data class AdminLogsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val items: List<AdminLogItemUi> = emptyList()
)

class AdminLogsViewModel : ViewModel() {

    private val sessionsRepo = Graph.chargingSessionRepository
    private val userRepo = Graph.userCardRepository

    private val _uiState = MutableStateFlow(AdminLogsUiState())
    val uiState: StateFlow<AdminLogsUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val sessions = sessionsRepo.getAll()
                val users = userRepo.getAll()
                val usersById = users.associateBy { it.id }

                val items = sessions.map { s ->
                    val user = usersById[s.userCardId]
                    val durationSeconds =
                        ((s.endTime - s.startTime).coerceAtLeast(0L)) / 1000L

                    AdminLogItemUi(
                        id = s.id,
                        userLogin = user?.login ?: "unknown",
                        userName = user?.name ?: "Unknown user",
                        startTimeMillis = s.startTime,
                        endTimeMillis = s.endTime,
                        durationSeconds = durationSeconds,
                        energyKwh = s.energyKwh,
                        totalPrice = s.totalPrice
                    )
                }

                _uiState.value = AdminLogsUiState(
                    isLoading = false,
                    items = items
                )
            } catch (e: Exception) {
                _uiState.value = AdminLogsUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load logs"
                )
            }
        }
    }
}
