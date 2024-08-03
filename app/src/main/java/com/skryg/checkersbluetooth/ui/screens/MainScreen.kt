package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skryg.checkersbluetooth.R
import com.skryg.checkersbluetooth.ui.NavigationDestination

object MainDestination: NavigationDestination(
    route = "main_screen",
    icon = Icons.Default.Home,
    name = "Main"
)

@Composable
fun MainScreen(){
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween){
        Row(
            Modifier.padding(16.dp)){
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End){
                Text("Checkers", fontWeight = FontWeight.Bold, fontSize = 45.sp)
                Text("Bluetooth", fontWeight = FontWeight.Bold, fontSize = 30.sp)

            }
            val image = painterResource(id = R.drawable.checkers_logo)
            Image(modifier = Modifier.size(130.dp)
                .clip(RoundedCornerShape(15.dp))
                .shadow(30.dp, RoundedCornerShape(15.dp)),
                painter = image,
                contentDescription = "CheckersBT logo")
        }
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally){
            TextButton(onClick = { /*TODO*/ }) {
                Text("Start Bluetooth game")

            }

            TextButton(onClick = { /*TODO*/ }) {
                Text("Start Local game")
            }
        }

    }
}