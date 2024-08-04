package com.skryg.checkersbluetooth.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.skryg.checkersbluetooth.ui.screens.ThemeChangeDestination
import com.skryg.checkersbluetooth.ui.screens.ThemeChangeScreen
import com.skryg.checkersbluetooth.ui.utils.DefaultTopAppBar
import com.skryg.checkersbluetooth.ui.utils.EmptyComposable
import com.skryg.checkersbluetooth.ui.utils.MenuBottomBar


abstract class NavigationDestination(
    val route: String,
    val defaultTopBar: Boolean = true,
    val topBarContent:  @Composable() () -> Unit = @Composable { },
    val defaultBottomBar: Boolean = true,
    val bottomBarContent:  @Composable() () -> Unit = @Composable { },
    val icon: ImageVector? = null,
    val name: String = ""
)

val navigationDestinations = mapOf(
    MainDestination.route to MainDestination,
    SavedGamesDestination.route to SavedGamesDestination,
    SettingsDestination.route to SettingsDestination,
    ThemeChangeDestination.route to ThemeChangeDestination
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navDest = navigationDestinations[currentRoute]

    Scaffold(
        topBar = if(navDest?.defaultTopBar == false){
            navDest.topBarContent
        } else {
            { if(navDest != null) DefaultTopAppBar(navController = navController, navDest = navDest) }
        },
        bottomBar = if (navDest?.defaultBottomBar == false) {
            navDest.bottomBarContent
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
                    SettingsScreen{
                        navController.navigate(ThemeChangeDestination.route)
                    }
                }
                composable(ThemeChangeDestination.route){
                    ThemeChangeScreen()
                }
            }
        }

    }
}