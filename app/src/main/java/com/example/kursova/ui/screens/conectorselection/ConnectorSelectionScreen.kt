package com.example.kursova.ui.screens.connectorselection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.kursova.domain.model.ConnectorStatus

@Composable
fun ConnectorSelectionScreen(
    onStartSession: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel = remember { ConnectorSelectionViewModel() }
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
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
                    modifier = Modifier.fillMaxSize(),
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Select connector",
                        style = MaterialTheme.typography.titleLarge
                    )

                    // список конекторів з усіма деталями
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.connectors) { item ->
                            ConnectorRow(
                                item = item,
                                onClick = { viewModel.onSelectConnector(item.id) }
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back")
                        }
                        Button(
                            onClick = { viewModel.startSession(onStartSession) },
                            enabled = state.selectedConnectorId != null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Start charging")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectorRow(
    item: ConnectorItemUi,
    onClick: () -> Unit
) {
    // підсвічуємо вибраний конектор
    val backgroundColor =
        if (item.isSelected)
            MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        else
            Color.Transparent

    val statusText = when (item.status) {
        ConnectorStatus.AVAILABLE -> "Available"
        ConnectorStatus.BUSY -> "Busy"
        ConnectorStatus.OUT_OF_ORDER -> "Out of order"
    }

    val statusColor = when (item.status) {
        ConnectorStatus.AVAILABLE -> MaterialTheme.colorScheme.primary
        ConnectorStatus.BUSY -> MaterialTheme.colorScheme.tertiary
        ConnectorStatus.OUT_OF_ORDER -> MaterialTheme.colorScheme.error
    }

    Surface(
        tonalElevation = if (item.isSelected) 2.dp else 0.dp,
        shadowElevation = if (item.isSelected) 2.dp else 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(
                enabled = item.status == ConnectorStatus.AVAILABLE,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = item.isSelected,
                onClick = onClick,
                enabled = item.status == ConnectorStatus.AVAILABLE
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Головний текст – НАЗВА порта
                Text(
                    text = if (item.name.isNotBlank()) item.name else "Unnamed connector",
                    style = MaterialTheme.typography.bodyLarge
                )

                // Додатково показуємо id та потужність
                Text(
                    text = "ID: ${item.id}, Power: ${"%.1f".format(item.powerKw)} kW",
                    style = MaterialTheme.typography.bodyMedium
                )

                // Статус з кольором
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
