package com.example.kursova.ui.screens.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.Connector
import com.example.kursova.domain.model.ConnectorStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConnectorItemUiState(
    val id: Int,
    val name: String,
    val maxPowerKw: Double,
    val status: ConnectorStatus
)

data class ManageConnectorsUiState(
    val isLoading: Boolean = true,
    val connectors: List<ConnectorItemUiState> = emptyList(),
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveMessage: String? = null
)

/**
 * Екран керування конекторами:
 * - показує всі конектори з локальної БД,
 * - дозволяє змінювати статуси,
 * - зберігає зміни локально та шле їх на сервер.
 */
class ManageConnectorsViewModel : ViewModel() {

    private val connectorRepo = Graph.connectorRepository

    private val _uiState = MutableStateFlow(ManageConnectorsUiState())
    val uiState: StateFlow<ManageConnectorsUiState> = _uiState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    saveMessage = null
                )

                val connectors = connectorRepo.getAll()

                _uiState.value = ManageConnectorsUiState(
                    isLoading = false,
                    connectors = connectors.map { it.toItemUi() },
                    error = null,
                    isSaving = false,
                    saveMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = ManageConnectorsUiState(
                    isLoading = false,
                    connectors = emptyList(),
                    error = e.message ?: "Failed to load connectors",
                    isSaving = false,
                    saveMessage = null
                )
            }
        }
    }

    fun onStatusChange(connectorId: Int, newStatus: ConnectorStatus) {
        val current = _uiState.value.connectors
        val updated = current.map {
            if (it.id == connectorId) it.copy(status = newStatus) else it
        }
        _uiState.value = _uiState.value.copy(
            connectors = updated,
            saveMessage = null
        )
    }

    fun saveChanges() {
        if (_uiState.value.isSaving) return

        viewModelScope.launch {
            val state = _uiState.value
            if (state.connectors.isEmpty()) return@launch

            try {
                _uiState.value = state.copy(
                    isSaving = true,
                    saveMessage = null,
                    error = null
                )

                // 1) Оновлюємо статуси в локальній БД
                for (item in state.connectors) {
                    connectorRepo.updateStatus(item.id, item.status)
                }

                // 2) Синхронізуємо всі конектори на сервер
                try {
                    connectorRepo.syncConnectorsToServer()
                } catch (_: Exception) {
                    // Якщо сервер недоступний – це не критично, локальні зміни вже збережені
                }

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveMessage = "Changes saved",
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save connectors",
                    saveMessage = null
                )
            }
        }
    }

    private fun Connector.toItemUi(): ConnectorItemUiState =
        ConnectorItemUiState(
            id = id,
            name = name,
            maxPowerKw = maxPowerKw,
            status = status
        )
}
