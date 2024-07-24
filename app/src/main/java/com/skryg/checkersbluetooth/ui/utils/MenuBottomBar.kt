package com.skryg.checkersbluetooth.ui.utils

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.skryg.checkersbluetooth.ui.screens.MainDestination
import com.skryg.checkersbluetooth.ui.screens.SavedGamesDestination
import com.skryg.checkersbluetooth.ui.screens.SettingsDestination

val bottomNav = listOf(
    MainDestination,
    SavedGamesDestination,
    SettingsDestination,
)

@Composable
fun MenuBottomBar(navController: NavHostController) {
    val currentRoute = navController.currentDestination?.route
    NavigationBar {
        bottomNav.forEach { destination ->
            NavigationBarItem(
                icon = { destination.icon?.let { Icon(it, contentDescription = null) } },
                label = { Text(destination.name) },
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}