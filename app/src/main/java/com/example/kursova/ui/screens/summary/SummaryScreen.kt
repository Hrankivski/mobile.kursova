package com.example.kursova.ui.screens.summary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SummaryScreen(
    sessionId: Long,
    onBackToMain: () -> Unit,
    onShowHistory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Summary for session #$sessionId (stub)")
            Button(onClick = onBackToMain) {
                Text("Back to main")
            }
            Button(onClick = onShowHistory) {
                Text("History")
            }
        }
    }
}
