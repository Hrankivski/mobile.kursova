package com.example.kursova.ui.screens.connectorselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.ConnectorStatus
import com.example.kursova.domain.repository.ChargingSessionRepository
import com.example.kursova.domain.repository.ConnectorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConnectorItemUi(
    val id: Int,
    val name: String,
    val powerKw: Double,
    val status: ConnectorStatus,
    val isSelected: Boolean
)

data class ConnectorSelectionUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val connectors: List<ConnectorItemUi> = emptyList(),
    val selectedConnectorId: Int? = null
)

class ConnectorSelectionViewModel(
    private val connectorRepository: ConnectorRepository = Graph.connectorRepository,
    private val sessionRepository: ChargingSessionRepository = Graph.chargingSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectorSelectionUiState())
    val uiState: StateFlow<ConnectorSelectionUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val connectors = connectorRepository.getAll()

                val items = connectors.map {
                    ConnectorItemUi(
                        id = it.id,
                        name = it.name,
                        powerKw = it.maxPowerKw,
                        status = it.status,
                        isSelected = false
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    connectors = items,
                    selectedConnectorId = null
                )
            } catch (e: Exception) {
                _uiState.value = ConnectorSelectionUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load connectors"
                )
            }
        }
    }

    fun onSelectConnector(id: Int) {
        val current = _uiState.value
        val updated = current.connectors.map { item ->
            item.copy(isSelected = item.id == id)
        }
        _uiState.value = current.copy(
            connectors = updated,
            selectedConnectorId = id
        )
    }

    fun startSession(onSessionCreated: (Long) -> Unit) {
        val state = _uiState.value
        val connectorId = state.selectedConnectorId ?: return

        val userId = Graph.currentUserId
        if (userId == null) {
            _uiState.value = state.copy(
                error = "User not logged in"
            )
            return
        }

        viewModelScope.launch {
            try {
                val sessionId = sessionRepository.createSession(
                    userCardId = userId,
                    connectorId = connectorId,
                    startTime = System.currentTimeMillis()
                )
                onSessionCreated(sessionId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to start session"
                )
            }
        }
    }
}
