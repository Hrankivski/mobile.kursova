package com.example.kursova.ui.screens.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ServiceHomeUiState(
    val isSyncing: Boolean = false,
    val syncError: String? = null,
    val syncSuccessMessage: String? = null
)

class ServiceHomeViewModel : ViewModel() {

    private val userRepo = Graph.userCardRepository
    private val tariffRepo = Graph.tariffRepository
    private val connectorRepo = Graph.connectorRepository

    private val _uiState = MutableStateFlow(ServiceHomeUiState())
    val uiState: StateFlow<ServiceHomeUiState> = _uiState

    fun syncAll() {
        if (_uiState.value.isSyncing) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSyncing = true,
                syncError = null,
                syncSuccessMessage = null
            )

            try {
                // 1) підтягнути користувачів із сервера
                userRepo.syncUsersDown()

                // 2) оновити тарифи із сервера
                tariffRepo.refreshFromServer()

                // 3) вивантажити локальні конектори на сервер
                connectorRepo.syncConnectorsToServer()

                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncError = null,
                    syncSuccessMessage = "Sync completed successfully"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncError = e.message ?: "Sync failed",
                    syncSuccessMessage = null
                )
            }
        }
    }
}
