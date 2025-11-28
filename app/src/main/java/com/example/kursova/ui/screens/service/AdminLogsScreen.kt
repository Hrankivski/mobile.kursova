package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kursova.core.util.TimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLogsScreen(
    onBack: () -> Unit
) {
    val viewModel = remember { AdminLogsViewModel() }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Charging logs") }
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
                        Button(onClick = { /* перезавантажити */ }) {
                            Text("Retry")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onBack) {
                            Text("Back")
                        }
                    }
                }

                state.items.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Наразі немає жодної сесії зарядки")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onBack) {
                            Text("Back")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.items) { item ->
                                AdminLogCard(item = item)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminLogCard(
    item: AdminLogItemUi
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
                text = "User: ${item.userName} (${item.userLogin})",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Energy: %.2f kWh".format(item.energyKwh),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Price: %.2f грн".format(item.totalPrice),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Duration: ${TimeUtils.formatDuration(item.durationSeconds)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
