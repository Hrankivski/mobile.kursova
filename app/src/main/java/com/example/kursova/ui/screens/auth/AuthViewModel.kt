package com.example.kursova.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.usecase.AuthenticateUserByPinUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: Int? = null
)

class AuthViewModel : ViewModel() {

    private val authUseCase = AuthenticateUserByPinUseCase(Graph.userCardRepository)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onPinChange(newPin: String) {
        _uiState.value = _uiState.value.copy(pin = newPin.take(4), error = null)
    }

    fun onSubmit() {
        val pin = _uiState.value.pin
        if (pin.length < 4) {
            _uiState.value = _uiState.value.copy(error = "PIN must be 4 digits")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val user = authUseCase(pin)
            if (user != null) {
                _uiState.value = _uiState.value.copy(isLoading = false, userId = user.id)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Invalid PIN"
                )
            }
        }
    }

    fun consumeUserId(): Int? {
        val id = _uiState.value.userId
        if (id != null) {
            _uiState.value = _uiState.value.copy(userId = null)
        }
        return id
    }
}
