package com.example.kursova.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.UserCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val login: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val userRepo = Graph.userCardRepository

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onLoginChange(value: String) {
        _uiState.value = _uiState.value.copy(login = value, error = null)
    }

    fun onPinChange(value: String) {
        _uiState.value = _uiState.value.copy(pin = value, error = null)
    }

    fun login(onSuccess: (UserCard) -> Unit) {
        val state = _uiState.value
        if (state.login.isBlank() || state.pin.isBlank()) {
            _uiState.value = state.copy(error = "Login and PIN are required")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                // 1) перед логіном пробуємо підтягнути користувачів із сервера
                try {
                    userRepo.syncUsersDown()
                } catch (_: Exception) {
                    // можна проігнорити помилку синку, логін все одно спробуємо локально
                }

                // 2) логін по локальній БД
                val user = userRepo.authenticate(state.login, state.pin)
                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Invalid login or PIN"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    onSuccess(user)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }
}
