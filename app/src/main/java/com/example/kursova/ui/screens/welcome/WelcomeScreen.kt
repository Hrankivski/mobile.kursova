package com.example.kursova.ui.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.example.kursova.Graph
import com.example.kursova.core.connection.ConnectionState

@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onSignUp: () -> Unit,
    onAdminMode: () -> Unit
) {
    val viewModel = remember { WelcomeViewModel() }
    val state = viewModel.uiState

    // стан підключення з Graph
    val connectionState by Graph.connectionState.collectAsState()

    // при першому показі екрана запускаємо початкову синхронізацію
    LaunchedEffect(Unit) {
        viewModel.initialSync()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // 🔹 Бейдж Online / Offline у правому верхньому куті
        ConnectionStatusBadge(
            state = connectionState,
            modifier = Modifier
                .align(Alignment.TopEnd)
        )
        // Основний контент розміщуємо по-центру
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "EV Charging Terminal",
                style = MaterialTheme.typography.headlineSmall
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
                    Text(
                        text = "Syncing data with server...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else if (state.syncError != null) {
                Text(
                    text = "Sync error: ${state.syncError}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onLogin) {
                Text("Login")
            }

            OutlinedButton(onClick = onSignUp) {
                Text("Sign up")
            }

            OutlinedButton(onClick = onAdminMode) {
                Text("Service / Admin mode")
            }
        }
    }
}

// Локальна маленька функція-бейдж, тільки для WelcomeScreen
@Composable
private fun ConnectionStatusBadge(
    state: ConnectionState,
    modifier: Modifier = Modifier
) {
    val (bgColor, text, icon, iconAlpha) = when (state) {
        ConnectionState.ONLINE -> arrayOf(
            MaterialTheme.colorScheme.primaryContainer,
            "Online",
            Icons.Filled.Cloud,
            1f
        )
        ConnectionState.OFFLINE -> arrayOf(
            MaterialTheme.colorScheme.errorContainer,
            "Offline",
            Icons.Filled.CloudOff,
            0.9f
        )
    }

    Row(
        modifier = modifier
            .background(
                color = bgColor as androidx.compose.ui.graphics.Color,
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector,
            contentDescription = null,
            modifier = Modifier.alpha(iconAlpha as Float),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = text as String,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
