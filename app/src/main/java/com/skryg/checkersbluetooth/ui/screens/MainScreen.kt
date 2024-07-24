package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import com.skryg.checkersbluetooth.ui.NavigationDestination

object MainDestination: NavigationDestination(
    route = "main_screen",
    bottomBarContent = null,
    icon = Icons.Default.Home,
    name = "Main"
)

@Composable
fun MainScreen(){

}