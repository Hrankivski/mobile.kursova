package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ServiceLoginScreen(
    onServiceLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { ServiceLoginViewModel() }
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
                text = "Service mode login",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = state.login,
                onValueChange = viewModel::onLoginChange,
                label = { Text("Admin login") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.pin,
                onValueChange = viewModel::onPinChange,
                label = { Text("Admin PIN (4 digits)") },
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
                onClick = { viewModel.onSubmit(onServiceLoginSuccess) },
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Checking..." else "Log in as admin")
            }

            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
