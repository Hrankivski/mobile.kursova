package com.example.kursova.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kursova.Graph
import com.example.kursova.domain.repository.UserCardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val login: String = "",
    val name: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class SignUpViewModel(
    private val userRepo: UserCardRepository = Graph.userCardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onLoginChange(v: String) {
        _uiState.value = _uiState.value.copy(login = v, error = null)
    }

    fun onNameChange(v: String) {
        _uiState.value = _uiState.value.copy(name = v, error = null)
    }

    fun onPinChange(v: String) {
        _uiState.value = _uiState.value.copy(pin = v, error = null)
    }

    fun onSubmit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.login.isBlank() || state.name.isBlank() || state.pin.length != 4) {
            _uiState.value = state.copy(error = "Fill all fields, PIN must be 4 digits")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isLoading = true, error = null)

                if (userRepo.isLoginTaken(state.login)) {
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = "Login already exists"
                    )
                    return@launch
                }

                val id = userRepo.createUser(
                    login = state.login,
                    name = state.name,
                    pinCode = state.pin,
                    isAdmin = false
                )

                Graph.currentUserId = id
                Graph.currentUserIsAdmin = false

                _uiState.value = state.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "Sign up failed"
                )
            }
        }
    }
}
