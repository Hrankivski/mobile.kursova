package com.example.kursova.ui.screens.charging

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChargingScreen(
    sessionId: Long,
    onSessionCompleted: (Long) -> Unit,
    onBack: () -> Unit
) {
    // Поки що просто кнопка "Finish session"
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
            Text("Charging session #$sessionId (stub)")
            Button(onClick = { onSessionCompleted(sessionId) }) {
                Text("Finish charging")
            }
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
