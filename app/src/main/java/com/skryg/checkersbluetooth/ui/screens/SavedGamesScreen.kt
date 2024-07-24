package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import com.skryg.checkersbluetooth.ui.NavigationDestination

object SavedGamesDestination: NavigationDestination(
    route = "saved_games_screen",
    bottomBarContent = null,
    icon = Icons.Default.Menu,
    name = "Saved Games"
)

@Composable
fun SavedGamesScreen(){

}