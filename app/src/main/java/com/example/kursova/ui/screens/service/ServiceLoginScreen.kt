package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kursova.domain.model.UserCard

@Composable
fun ServiceLoginScreen(
    onServiceLoginSuccess: (UserCard) -> Unit,
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
                text = "Service / Admin login",
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
                label = { Text("PIN") }
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
                    viewModel.login { adminUser ->
                        onServiceLoginSuccess(adminUser)
                    }
                },
                enabled = !state.isLoading
            ) {
                Text("Login as admin")
            }

            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
