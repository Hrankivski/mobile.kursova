package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kursova.core.util.TimeUtils

@Composable
fun AdminLogsScreen(
    onBack: () -> Unit
) {
    val viewModel = remember { AdminLogsViewModel() }
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Charging logs (admin)",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(onClick = onBack) {
                            Text("Back")
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items) { item ->
                            AdminLogRow(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminLogRow(item: AdminLogItemUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Session #${item.id} - ${item.userLogin}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "User: ${item.userName}",
            style = MaterialTheme.typography.bodyMedium
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
