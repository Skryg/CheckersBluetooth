package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.skryg.checkersbluetooth.ui.NavigationDestination

object MainDestination: NavigationDestination(
    route = "main_screen",
    bottomBarContent = null,
    icon = Icons.Default.Home,
    name = "Main"
)

@Composable
fun MainScreen(){
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text("Checkers")
        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))
        TextButton(onClick = { /*TODO*/ }) {
            Text("Start Local game")
        }
    }
}