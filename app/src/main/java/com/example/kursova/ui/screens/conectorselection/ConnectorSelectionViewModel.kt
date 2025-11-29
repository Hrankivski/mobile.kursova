package com.example.kursova.ui.screens.connectorselection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.Connector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConnectorItemUi(
    val id: Int,
    val name: String,
    val maxPowerKw: Double,
    val isSelected: Boolean
)

data class ConnectorSelectionUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val connectors: List<ConnectorItemUi> = emptyList(),
    val isStarting: Boolean = false
)

class ConnectorSelectionViewModel : ViewModel() {

    private val connectorRepo = Graph.connectorRepository
    private val sessionRepo = Graph.chargingSessionRepository

    private val _uiState = MutableStateFlow(ConnectorSelectionUiState())
    val uiState: StateFlow<ConnectorSelectionUiState> = _uiState

    private var selectedConnectorId: Int? = null

    init {
        loadConnectors()
    }

    private fun loadConnectors() {
        viewModelScope.launch {
            try {
                _uiState.value = ConnectorSelectionUiState(isLoading = true)

                val connectors: List<Connector> = connectorRepo.getAvailable()
                val items = connectors.map {
                    ConnectorItemUi(
                        id = it.id,
                        name = it.name,
                        maxPowerKw = it.maxPowerKw,
                        isSelected = false
                    )
                }

                _uiState.value = ConnectorSelectionUiState(
                    isLoading = false,
                    connectors = items,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = ConnectorSelectionUiState(
                    isLoading = false,
                    connectors = emptyList(),
                    error = e.message ?: "Failed to load connectors"
                )
            }
        }
    }

    fun onConnectorClick(id: Int) {
        selectedConnectorId = id
        _uiState.value = _uiState.value.copy(
            connectors = _uiState.value.connectors.map {
                it.copy(isSelected = it.id == id)
            }
        )
    }

    fun startSession(onSessionCreated: (Long) -> Unit) {
        val userId = Graph.currentUserId
            ?: throw IllegalStateException("User is not logged in")
        Log.d("DebugSession", "Starting session for userId=$userId, connectorId=$selectedConnectorId")


        val connectorId = selectedConnectorId
            ?: return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isStarting = true, error = null)

                val now = System.currentTimeMillis()
                val sessionId = sessionRepo.createSession(
                    userCardId = userId,
                    connectorId = connectorId,
                    startTime = now
                )

                _uiState.value = _uiState.value.copy(isStarting = false)
                onSessionCreated(sessionId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isStarting = false,
                    error = e.message ?: "Failed to start session"
                )
            }
        }
    }

}
