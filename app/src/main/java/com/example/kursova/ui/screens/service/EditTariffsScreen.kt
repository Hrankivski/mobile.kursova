package com.example.kursova.ui.screens.service

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTariffsScreen(
    onBack: () -> Unit
) {
    val viewModel = remember { EditTariffsViewModel() }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tariff settings") }
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

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (state.error != null) {
                                Text(
                                    text = state.error ?: "",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            if (state.successMessage != null) {
                                Text(
                                    text = state.successMessage ?: "",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            OutlinedTextField(
                                value = state.dayPriceInput,
                                onValueChange = viewModel::onDayPriceChange,
                                label = { Text("Day price, грн/кВт·год") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = state.nightPriceInput,
                                onValueChange = viewModel::onNightPriceChange,
                                label = { Text("Night price, грн/кВт·год") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = state.nightStartInput,
                                onValueChange = viewModel::onNightStartChange,
                                label = { Text("Night start hour (0–23)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = state.nightEndInput,
                                onValueChange = viewModel::onNightEndChange,
                                label = { Text("Night end hour (0–23)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                            OutlinedButton(
                                onClick = { viewModel.onResetToCurrent() },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Reset")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.onSave() },
                                enabled = !state.isSaving,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (state.isSaving) "Saving..." else "Save")
                            }
                        }
                    }
                }
            }
        }
    }
}
