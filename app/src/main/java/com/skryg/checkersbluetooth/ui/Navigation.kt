package com.skryg.checkersbluetooth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.skryg.checkersbluetooth.ui.screens.MainDestination
import com.skryg.checkersbluetooth.ui.screens.MainScreen
import com.skryg.checkersbluetooth.ui.screens.SavedGamesDestination
import com.skryg.checkersbluetooth.ui.screens.SavedGamesScreen
import com.skryg.checkersbluetooth.ui.screens.SettingsDestination
import com.skryg.checkersbluetooth.ui.screens.SettingsScreen
import com.skryg.checkersbluetooth.ui.utils.MenuBottomBar


abstract class NavigationDestination(
    val route: String,
    val topBarContent: @Composable (() -> Unit)? = {},
    val bottomBarContent: @Composable() (() -> Unit)? = {},
    val icon: ImageVector? = null,
    val name: String = ""
)

val navigationDestinations = listOf(
    MainDestination,
    SavedGamesDestination,
    SettingsDestination
)
val navMaps by lazy { navigationDestinations.associateBy { it.route } }


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = navMaps[currentRoute]?.topBarContent ?: {},
        bottomBar = if (navMaps[currentRoute]?.bottomBarContent != null) {
            navMaps[currentRoute]?.bottomBarContent ?: {}
        } else {
            { MenuBottomBar(navController = navController) }
        }
    ){
        Column(modifier = Modifier.padding(it)){
            NavHost(navController = navController, startDestination = MainDestination.route){
                composable(MainDestination.route) {
                    MainScreen()
                }
                composable(SavedGamesDestination.route) {
                    SavedGamesScreen()
                }
                composable(SettingsDestination.route) {
                    SettingsScreen()
                }
            }
        }

    }
}