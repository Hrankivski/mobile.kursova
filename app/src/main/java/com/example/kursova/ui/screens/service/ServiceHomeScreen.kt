package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ServiceHomeScreen(
    onManageConnectors: () -> Unit,
    onEditTariffs: () -> Unit,
    onViewLogs: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { ServiceHomeViewModel() }
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
                text = "Service / Admin panel",
                style = MaterialTheme.typography.titleLarge
            )

            if (state.isSyncing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Text("Sync in progress...")
                }
            }

            if (state.syncError != null) {
                Text(
                    text = "Sync error: ${state.syncError}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (state.syncSuccessMessage != null) {
                Text(
                    text = state.syncSuccessMessage!!,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onManageConnectors) {
                Text("Manage connectors")
            }

            Button(
                onClick = onEditTariffs,
            ) {
                Text("Edit tariffs")
            }

            Button(onClick = onViewLogs) {
                Text("View charging logs")
            }

            OutlinedButton(
                onClick = { viewModel.syncAll() },
                enabled = !state.isSyncing
            ) {
                Text("Sync with server")
            }

            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
