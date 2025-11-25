package com.example.kursova.ui.screens.connectorselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConnectorSelectionScreen(
    userId: Int,
    onStartSession: (Long) -> Unit,
    onBack: () -> Unit
) {
    // Для простоти створюємо VM через remember
    val viewModel = remember { ConnectorSelectionViewModel(userId) }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.createdSessionId) {
        val id = viewModel.consumeSessionId()
        if (id != null) onStartSession(id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Верхній "хедер" замість TopAppBar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select connector",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onBack) {
                Text("< Back")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Available connectors:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Список конекторів
        state.connectors.forEach { connector ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { viewModel.onConnectorSelected(connector.id) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(connector.name, style = MaterialTheme.typography.bodyLarge)
                    Text("${connector.maxPowerKw} kW", style = MaterialTheme.typography.bodyMedium)

                    if (state.selectedConnectorId == connector.id) {
                        Text(
                            "Selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Charging time (minutes):",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(15, 30, 60).forEach { minutes ->
                OutlinedButton(
                    onClick = { viewModel.onMinutesChange(minutes) },
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = if (state.selectedMinutes == minutes) {
                            "$minutes min ✓"
                        } else {
                            "$minutes min"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onStartSession() },
            enabled = state.selectedConnectorId != null && !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (state.isLoading) "Starting..." else "Start charging")
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
