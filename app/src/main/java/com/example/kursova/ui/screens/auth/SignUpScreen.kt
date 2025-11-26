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
                text = "Sign up",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = state.login,
                onValueChange = viewModel::onLoginChange,
                label = { Text("Login") },
                singleLine = true
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name") },
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
                onClick = { viewModel.onSubmit(onSignUpSuccess) },
                enabled = !state.isLoading
            ) {
                Text(if (state.isLoading) "Creating..." else "Create account")
            }

            OutlinedButton(onClick = onGoToLogin) {
                Text("Back to login")
            }
        }
    }
}
