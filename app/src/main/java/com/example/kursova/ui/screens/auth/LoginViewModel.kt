package com.example.kursova.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.repository.UserCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val login: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val userRepo: UserCardRepository = Graph.userCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onLoginChange(value: String) {
        _uiState.value = _uiState.value.copy(login = value, error = null)
    }

    fun onPinChange(value: String) {
        _uiState.value = _uiState.value.copy(pin = value, error = null)
    }

    fun onSubmit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.login.isBlank() || state.pin.length != 4) {
            _uiState.value = state.copy(error = "Enter login and 4-digit PIN")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)
                val user = userRepo.getByLogin(state.login)

                if (user == null || user.pinCode != state.pin) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Invalid login or PIN"
                    )
                    return@launch
                }

                Graph.currentUserId = user.id
                Graph.currentUserIsAdmin = user.isAdmin

                _uiState.value = state.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }
}
