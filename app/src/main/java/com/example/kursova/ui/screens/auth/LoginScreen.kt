package com.example.kursova.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
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
                text = "Log in",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = state.login,
                onValueChange = viewModel::onLoginChange,
                label = { Text("Login") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.pin,
                onValueChange = viewModel::onPinChange,
                label = { Text("PIN (4 digits)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { viewModel.onSubmit(onLoginSuccess) },
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Logging in..." else "Log in")
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
