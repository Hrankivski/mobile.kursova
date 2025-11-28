package com.example.kursova.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun HistoryScreen(
    onBack: () -> Unit
) {
    val viewModel = remember { HistoryViewModel() }
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Charging history",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = onBack) {
                            Text("Back")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.items) { item ->
                            HistoryItemRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemRow(item: HistoryItemUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Session #${item.id}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Start: ${TimeUtils.formatTime(item.startTimeMillis)}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Duration: ${TimeUtils.formatDuration(item.durationSeconds)}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = String.format("Energy: %.2f kWh", item.energyKwh),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = String.format("Total: %.2f грн", item.totalPrice),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
