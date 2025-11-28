package com.example.kursova.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val login: String = "",
    val pin: String = "",
    val name: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class SignUpViewModel : ViewModel() {

    private val userRepo = Graph.userCardRepository

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onLoginChange(value: String) {
        _uiState.value = _uiState.value.copy(login = value, error = null)
    }

    fun onPinChange(value: String) {
        _uiState.value = _uiState.value.copy(pin = value, error = null)
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value, error = null)
    }

    fun signUp(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.login.isBlank() || state.pin.isBlank() || state.name.isBlank()) {
            _uiState.value = state.copy(error = "All fields are required")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                // 1) створюємо локального користувача
                userRepo.signUp(
                    login = state.login,
                    pinCode = state.pin,
                    name = state.name
                )

                // 2) пробуємо відвантажити на сервер
                try {
                    userRepo.syncUsersUp()
                } catch (_: Exception) {
                    // якщо сервер недоступний, користувач все одно є локально
                }

                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Sign up failed"
                )
            }
        }
    }
}
