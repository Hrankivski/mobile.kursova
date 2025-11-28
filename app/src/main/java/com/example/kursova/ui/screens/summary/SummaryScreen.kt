package com.example.kursova.ui.screens.summary

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
fun SummaryScreen(
    sessionId: Long,
    onBackToMain: () -> Unit,
    onShowHistory: () -> Unit
) {
    val viewModel = remember { SummaryViewModel(sessionId) }
    val state by viewModel.uiState.collectAsState()

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
                    OutlinedButton(onClick = onBackToMain) {
                        Text("Back")
                    }
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Charging summary",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Connector: ${state.connectorName} (${state.powerKw} kW)")
                    Text("Start: ${TimeUtils.formatTime(state.startTimeMillis)}")
                    Text("End: ${TimeUtils.formatTime(state.endTimeMillis)}")
                    Text("Duration: ${TimeUtils.formatDuration(state.durationSeconds)}")
                    Text(String.format("Energy: %.2f kWh", state.energyKwh))
                    Text(String.format("Total: %.2f грн", state.totalPrice))

                    if (state.tariffUsed.isNotBlank()) {
                        Text("Tariff: ${state.tariffUsed}")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onBackToMain, modifier = Modifier.fillMaxWidth()) {
                        Text("Back to main")
                    }

                    OutlinedButton(
                        onClick = onShowHistory,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("History")
                    }
                }
            }
        }
    }
}
