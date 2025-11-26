package com.example.kursova.ui.screens.charging

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kursova.core.util.TimeUtils

@Composable
fun ChargingScreen(
    sessionId: Long,
    onSessionCompleted: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { ChargingViewModel(sessionId) }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onSessionCompleted(sessionId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.error != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = state.error ?: "Error",
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Charging...",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Connector: ${state.connectorName} (${state.powerKw} kW)",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Time: ${TimeUtils.formatDuration(state.elapsedSeconds)}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = String.format("Energy: %.2f kWh", state.energyKwh),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = String.format("Price: %.2f грн", state.totalPrice),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (state.tariffLabel.isNotBlank()) {
                        Text(
                            text = "Tariff: ${state.tariffLabel}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.onFinishCharging() },
                        enabled = !state.isCompleting
                    ) {
                        Text(if (state.isCompleting) "Finishing..." else "Finish charging")
                    }

                    OutlinedButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            }
        }
    }
}
