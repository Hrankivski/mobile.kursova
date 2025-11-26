package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
                text = "Service mode",
                style = MaterialTheme.typography.titleLarge
            )

            Button(onClick = onManageConnectors) {
                Text("Manage connectors")
            }

            Button(onClick = onEditTariffs) {
                Text("Edit tariffs")
            }

            Button(onClick = onViewLogs) {
                Text("View charging logs")
            }

            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
