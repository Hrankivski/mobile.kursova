package com.example.kursova.ui.screens.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.model.UserCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ServiceLoginUiState(
    val login: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Логін у сервісний режим:
 * - тягнемо користувачів із сервера (down-sync),
 * - шукаємо користувача локально,
 * - перевіряємо, що це адмін (isAdmin = true).
 */
class ServiceLoginViewModel : ViewModel() {

    private val userRepo = Graph.userCardRepository

    private val _uiState = MutableStateFlow(ServiceLoginUiState())
    val uiState: StateFlow<ServiceLoginUiState> = _uiState

    fun onLoginChange(value: String) {
        _uiState.value = _uiState.value.copy(login = value, error = null)
    }

    fun onPinChange(value: String) {
        _uiState.value = _uiState.value.copy(pin = value, error = null)
    }

    fun login(onSuccess: (UserCard) -> Unit) {
        val state = _uiState.value
        if (state.login.isBlank() || state.pin.isBlank()) {
            _uiState.value = state.copy(error = "Login та PIN обов'язкові")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // 1) Спробуємо підтягнути користувачів із сервера
                try {
                    userRepo.syncUsersDown()
                } catch (_: Exception) {
                    // сервер може бути недоступний - тоді працюємо з локальними даними
                }

                // 2) Локальна автентифікація
                val user = userRepo.authenticate(state.login, state.pin)

                if (user == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Невірний логін або PIN"
                    )
                    return@launch
                }

                if (!user.isAdmin) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Користувач не має прав сервісного доступу"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                onSuccess(user)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Service login failed"
                )
            }
        }
    }
}
