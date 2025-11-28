package com.example.kursova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kursova.Graph
import com.example.kursova.domain.model.UserCard
import com.example.kursova.ui.screens.charging.ChargingScreen
import com.example.kursova.ui.screens.connectorselection.ConnectorSelectionScreen
import com.example.kursova.ui.screens.history.HistoryScreen
import com.example.kursova.ui.screens.summary.SummaryScreen
import com.example.kursova.ui.screens.welcome.WelcomeScreen
import com.example.kursova.ui.screens.auth.LoginScreen
import com.example.kursova.ui.screens.auth.SignUpScreen
import com.example.kursova.ui.screens.service.ServiceLoginScreen
import com.example.kursova.ui.screens.service.ServiceHomeScreen
import com.example.kursova.ui.screens.service.ManageConnectorsScreen
import com.example.kursova.ui.screens.service.AdminLogsScreen
import com.example.kursova.ui.screens.service.EditTariffsScreen

object Routes {
    const val WELCOME = "welcome"
    const val AUTH = "auth"
    const val SIGN_UP = "sign_up"
    const val CONNECTOR_SELECTION = "connectorSelection"
    const val CHARGING = "charging"
    const val SUMMARY = "summary"
    const val HISTORY = "history"

    const val ADMIN = "admin"
    const val SERVICE_HOME = "service_home"
    const val MANAGE_CONNECTORS = "manage_connectors"
    const val ADMIN_LOGS = "admin_logs"
    const val EDIT_TARIFFS = "edit_tariffs"
    //const val ARG_USER_ID = "userId"
    const val ARG_SESSION_ID = "sessionId"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        // ---------------- WELCOME ----------------
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLogin = { navController.navigate(Routes.AUTH) },
                onSignUp = { navController.navigate(Routes.SIGN_UP) },
                onAdminMode = { navController.navigate(Routes.ADMIN) }
            )
        }

        // ---------------- AUTH: LOGIN ----------------
        composable(Routes.AUTH) {
            LoginScreen(
                onLoginSuccess = {
                    user: UserCard ->
                    Graph.currentUserId = user.id
                    navController.navigate(Routes.CONNECTOR_SELECTION)
                },
                onGoToSignUp = {
                    navController.navigate(Routes.SIGN_UP)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------- AUTH: SIGN UP ----------------
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onSignUpSuccess = {
                    // після створення акаунту так само йдемо до вибору конектора
                    navController.navigate(Routes.CONNECTOR_SELECTION)
                },
                onGoToLogin = {
                    navController.popBackStack() // повертаємось до LOGIN (AUTH)
                }
            )
        }

        // ---------------- MAIN FLOW: CONNECTOR SELECTION ----------------
        // Раніше передавався userId в route, тепер екран може брати Graph.currentUserId
        composable(Routes.CONNECTOR_SELECTION) {
            ConnectorSelectionScreen(
                onStartSession = { sessionId ->
                    navController.navigate("${Routes.CHARGING}/$sessionId")
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------- MAIN FLOW: CHARGING ----------------
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
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------- MAIN FLOW: SUMMARY ----------------
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

        // ---------------- MAIN FLOW: HISTORY ----------------
        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ---------------- SERVICE MODE: LOGIN ----------------
        // Раніше тут був AdminStubScreen, тепер повноцінний сервіс-логін
        composable(Routes.ADMIN) {
            ServiceLoginScreen(
                onServiceLoginSuccess = {
                    navController.navigate(Routes.SERVICE_HOME)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------- SERVICE MODE: HOME ----------------
        composable(Routes.SERVICE_HOME) {
            ServiceHomeScreen(
                onManageConnectors = {
                    navController.navigate(Routes.MANAGE_CONNECTORS)
                },
                onEditTariffs = {
                    navController.navigate(Routes.EDIT_TARIFFS)

                },
                onViewLogs = {
                    navController.navigate(Routes.ADMIN_LOGS)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------- SERVICE MODE: MANAGE CONNECTORS ----------------
        composable(Routes.MANAGE_CONNECTORS) {
            ManageConnectorsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ---------------- SERVICE MODE: ADMIN LOGS ----------------
        composable(Routes.ADMIN_LOGS) {
            AdminLogsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ---------------- SERVICE MODE: EDIT TARIFFS ----------------
        composable(Routes.EDIT_TARIFFS) {
            EditTariffsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
