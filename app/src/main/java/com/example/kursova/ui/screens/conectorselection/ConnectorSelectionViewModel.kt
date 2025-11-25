package com.example.kursova.ui.screens.connectorselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.usecase.GetAvailableConnectorsUseCase
import com.example.kursova.domain.usecase.StartChargingSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConnectorSelectionUiState(
    val connectors: List<Connector> = emptyList(),
    val selectedConnectorId: Int? = null,
    val selectedMinutes: Int = 30,
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdSessionId: Long? = null
)

class ConnectorSelectionViewModel(
    private val userId: Int
) : ViewModel() {

    private val getConnectorsUseCase = GetAvailableConnectorsUseCase(Graph.connectorRepository)
    private val startSessionUseCase = StartChargingSessionUseCase(Graph.chargingSessionRepository)

    private val _uiState = MutableStateFlow(ConnectorSelectionUiState())
    val uiState: StateFlow<ConnectorSelectionUiState> = _uiState

    init {
        loadConnectors()
    }

    private fun loadConnectors() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val connectors = getConnectorsUseCase()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                connectors = connectors
            )
        }
    }

    fun onConnectorSelected(id: Int) {
        _uiState.value = _uiState.value.copy(selectedConnectorId = id)
    }

    fun onMinutesChange(minutes: Int) {
        _uiState.value = _uiState.value.copy(selectedMinutes = minutes)
    }

    fun onStartSession() {
        val connectorId = _uiState.value.selectedConnectorId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val sessionId = startSessionUseCase(
                userCardId = userId,
                connectorId = connectorId,
                startTime = System.currentTimeMillis()
            )
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                createdSessionId = sessionId
            )
        }
    }

    fun consumeSessionId(): Long? {
        val id = _uiState.value.createdSessionId
        if (id != null) {
            _uiState.value = _uiState.value.copy(createdSessionId = null)
        }
        return id
    }
}
