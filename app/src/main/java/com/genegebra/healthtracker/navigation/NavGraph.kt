package com.genegebra.healthtracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.genegebra.healthtracker.domain.repository.AuthRepository
import com.genegebra.healthtracker.presentation.auth.AuthScreen
import com.genegebra.healthtracker.presentation.entry.EntryScreen
import com.genegebra.healthtracker.presentation.history.HistoryScreen

private sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Entry : Screen("entry", "Log", Icons.Default.AddCircle)
    data object History : Screen("history", "History", Icons.Default.History)
}

private val bottomNavScreens = listOf(Screen.Entry, Screen.History)

@Composable
fun AppNavGraph(authRepository: AuthRepository) {
    val rootNav = rememberNavController()
    val currentUser by authRepository.currentUser.collectAsState(initial = null)

    NavHost(navController = rootNav, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                onAuthenticated = {
                    rootNav.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainScaffold(
                onLogout = {
                    rootNav.navigate("auth") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun MainScaffold(onLogout: () -> Unit) {
    val bottomNav = rememberNavController()
    val navBackStackEntry by bottomNav.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNav.navigate(screen.route) {
                                popUpTo(bottomNav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNav,
            startDestination = Screen.Entry.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Screen.Entry.route) {
                EntryScreen(
                    onNavigateToHistory = {
                        bottomNav.navigate(Screen.History.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToEntry = {
                        bottomNav.navigate(Screen.Entry.route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
