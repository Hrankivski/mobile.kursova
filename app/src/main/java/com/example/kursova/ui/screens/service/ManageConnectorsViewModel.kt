package com.example.kursova.ui.screens.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.ConnectorStatus
import com.example.kursova.domain.repository.ConnectorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiConnector(
    val id: Int,
    val name: String,
    val powerKw: Double,
    val isAvailable: Boolean
)

data class ManageConnectorsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val items: List<UiConnector> = emptyList()
)

class ManageConnectorsViewModel(
    private val repo: ConnectorRepository = Graph.connectorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageConnectorsUiState())
    val uiState: StateFlow<ManageConnectorsUiState> = _uiState

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val connectors = repo.getAll()
                val uiItems = connectors.map {
                    UiConnector(
                        id = it.id,
                        name = it.name,
                        powerKw = it.maxPowerKw,
                        isAvailable = it.status == ConnectorStatus.AVAILABLE
                    )
                }

                _uiState.value = ManageConnectorsUiState(
                    isLoading = false,
                    items = uiItems
                )
            } catch (e: Exception) {
                _uiState.value = ManageConnectorsUiState(
                    isLoading = false,
                    error = e.message ?: "Failed to load connectors"
                )
            }
        }
    }

    fun onToggle(id: Int, newValue: Boolean) {
        viewModelScope.launch {
            try {
                val newStatus =
                    if (newValue) ConnectorStatus.AVAILABLE else ConnectorStatus.OUT_OF_ORDER
                repo.updateStatus(id, newStatus)
                load()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to update connector"
                )
            }
        }
    }
}
