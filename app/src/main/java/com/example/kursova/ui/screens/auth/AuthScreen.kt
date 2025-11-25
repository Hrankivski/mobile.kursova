package com.example.kursova.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AuthScreen(
    onAuthSuccess: (Int) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.userId) {
        val id = viewModel.consumeUserId()
        if (id != null) {
            onAuthSuccess(id)
        }
    }

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
            Text(text = "Driver Authorization")

            OutlinedTextField(
                value = state.pin,
                onValueChange = viewModel::onPinChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                label = { Text("PIN") },
                singleLine = true
            )

            if (state.error != null) {
                Text(text = state.error ?: "", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.onSubmit() },
                enabled = !state.isLoading
            ) {
                Text(text = if (state.isLoading) "..." else "Continue")
            }
        }
    }
}
