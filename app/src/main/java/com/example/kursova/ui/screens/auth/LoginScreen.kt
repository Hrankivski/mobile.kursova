package com.example.kursova.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kursova.domain.model.UserCard

@Composable
fun LoginScreen(
    onLoginSuccess: (UserCard) -> Unit,
    onGoToSignUp: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { LoginViewModel() }
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "User login",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = state.login,
                onValueChange = { viewModel.onLoginChange(it) },
                label = { Text("Login") }
            )

            OutlinedTextField(
                value = state.pin,
                onValueChange = { viewModel.onPinChange(it) },
                label = { Text("PIN code") }
            )

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            Button(
                onClick = {
                    viewModel.login { user ->
                        onLoginSuccess(user)
                    }
                },
                enabled = !state.isLoading
            ) {
                Text("Login")
            }

            OutlinedButton(onClick = onGoToSignUp) {
                Text("Sign up")
            }

            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
