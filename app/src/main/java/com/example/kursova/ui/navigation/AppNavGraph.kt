package com.example.kursova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kursova.ui.screens.admin.AdminStubScreen
import com.example.kursova.ui.screens.auth.AuthScreen
import com.example.kursova.ui.screens.charging.ChargingScreen
import com.example.kursova.ui.screens.connectorselection.ConnectorSelectionScreen
import com.example.kursova.ui.screens.history.HistoryScreen
import com.example.kursova.ui.screens.summary.SummaryScreen
import com.example.kursova.ui.screens.welcome.WelcomeScreen

object Routes {
    const val WELCOME = "welcome"
    const val AUTH = "auth"
    const val CONNECTOR_SELECTION = "connectorSelection"
    const val CHARGING = "charging"
    const val SUMMARY = "summary"
    const val HISTORY = "history"
    const val ADMIN = "admin"

    const val ARG_USER_ID = "userId"
    const val ARG_SESSION_ID = "sessionId"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onStartCharging = { navController.navigate(Routes.AUTH) },
                onAdminMode = { navController.navigate(Routes.ADMIN) }
            )
        }

        composable(Routes.AUTH) {
            AuthScreen(
                onAuthSuccess = { userId ->
                    navController.navigate("${Routes.CONNECTOR_SELECTION}/$userId")
                }
            )
        }

        composable(
            route = "${Routes.CONNECTOR_SELECTION}/{${Routes.ARG_USER_ID}}",
            arguments = listOf(
                navArgument(Routes.ARG_USER_ID) { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt(Routes.ARG_USER_ID) ?: 0
            ConnectorSelectionScreen(
                userId = userId,
                onStartSession = { sessionId ->
                    navController.navigate("${Routes.CHARGING}/$sessionId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.CHARGING}/{${Routes.ARG_SESSION_ID}}",
            arguments = listOf(
                navArgument(Routes.ARG_SESSION_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong(Routes.ARG_SESSION_ID) ?: 0L
            ChargingScreen(
                sessionId = sessionId,
                onSessionCompleted = { completedSessionId ->
                    navController.navigate("${Routes.SUMMARY}/$completedSessionId") {
                        popUpTo(Routes.WELCOME) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.SUMMARY}/{${Routes.ARG_SESSION_ID}}",
            arguments = listOf(
                navArgument(Routes.ARG_SESSION_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong(Routes.ARG_SESSION_ID) ?: 0L
            SummaryScreen(
                sessionId = sessionId,
                onBackToMain = {
                    navController.popBackStack(Routes.WELCOME, false)
                },
                onShowHistory = {
                    navController.navigate(Routes.HISTORY)
                }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.ADMIN) {
            AdminStubScreen(onBack = { navController.popBackStack() })
        }
    }
}
