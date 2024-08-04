package com.skryg.checkersbluetooth.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.skryg.checkersbluetooth.ui.NavigationDestination

@ExperimentalMaterial3Api
@Composable
fun DefaultTopAppBar(navController: NavHostController, navDest: NavigationDestination){
    TopAppBar(
        title = { Text(navDest.name) },
        navigationIcon = {
            IconButton(onClick = {navController.popBackStack()}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}