package com.example.kursova.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val viewModel = remember { SignUpViewModel() }
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
                text = "Create account",
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

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = { Text("Name") }
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
                    viewModel.signUp {
                        onSignUpSuccess()
                    }
                },
                enabled = !state.isLoading
            ) {
                Text("Sign up")
            }

            OutlinedButton(onClick = onGoToLogin) {
                Text("Back to login")
            }
        }
    }
}
