package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kursova.domain.model.ConnectorStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageConnectorsScreen(
    onBack: () -> Unit
) {
    val viewModel = remember { ManageConnectorsViewModel() }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage connectors") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.error ?: "Error",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.load() }) {
                            Text("Retry")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onBack) {
                            Text("Back")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f, fill = true)
                        ) {
                            if (state.saveMessage != null) {
                                Text(
                                    text = state.saveMessage!!,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(state.connectors) { item ->
                                    ConnectorCard(
                                        item = item,
                                        onStatusChange = { newStatus ->
                                            viewModel.onStatusChange(item.id, newStatus)
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { viewModel.saveChanges() },
                                enabled = !state.isSaving,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (state.isSaving) "Saving..." else "Save changes")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectorCard(
    item: ConnectorItemUiState,
    onStatusChange: (ConnectorStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = String.format("Max power: %.1f kW", item.maxPowerKw),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Status:",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(
                    label = "Available",
                    selected = item.status == ConnectorStatus.AVAILABLE,
                    onClick = { onStatusChange(ConnectorStatus.AVAILABLE) }
                )
                StatusChip(
                    label = "Busy",
                    selected = item.status == ConnectorStatus.BUSY,
                    onClick = { onStatusChange(ConnectorStatus.BUSY) }
                )
                StatusChip(
                    label = "Out of order",
                    selected = item.status == ConnectorStatus.OUT_OF_ORDER,
                    onClick = { onStatusChange(ConnectorStatus.OUT_OF_ORDER) }
                )
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Button(onClick = onClick) {
            Text(label)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label)
        }
    }
}
