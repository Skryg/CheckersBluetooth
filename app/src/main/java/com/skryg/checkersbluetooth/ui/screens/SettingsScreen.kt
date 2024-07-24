package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import com.skryg.checkersbluetooth.ui.NavigationDestination

object SettingsDestination: NavigationDestination(
    route = "settings_screen",
    bottomBarContent = null,
    icon = Icons.Default.Settings,
    name = "Settings"
)

@Composable
fun SettingsScreen(){

}