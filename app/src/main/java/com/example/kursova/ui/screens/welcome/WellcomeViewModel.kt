package com.example.kursova.ui.screens.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import kotlinx.coroutines.launch

data class WelcomeUiState(
    val isSyncing: Boolean = false,
    val syncError: String? = null
)

class WelcomeViewModel : ViewModel() {

    private val userRepo = Graph.userCardRepository
    private val tariffRepo = Graph.tariffRepository

    var uiState: WelcomeUiState = WelcomeUiState()
        private set

    fun initialSync() {
        if (uiState.isSyncing) return

        viewModelScope.launch {
            uiState = uiState.copy(isSyncing = true, syncError = null)
            try {
                userRepo.syncUsersDown()
                tariffRepo.refreshFromServer()

                Graph.markOnline()

                uiState = uiState.copy(isSyncing = false, syncError = null)

            } catch (e: Exception) {
                Graph.markOffline()

                uiState = uiState.copy(
                    isSyncing = false,
                    syncError = e.message ?: "Initial sync failed"
                )
            }
        }
    }
}
