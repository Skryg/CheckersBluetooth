package com.skryg.checkersbluetooth.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.skryg.checkersbluetooth.MainActivity
import com.skryg.checkersbluetooth.R
import com.skryg.checkersbluetooth.game.ui.utils.LittleBoard
import com.skryg.checkersbluetooth.ui.NavigationDestination

@OptIn(ExperimentalMaterial3Api::class)
object SettingsDestination: NavigationDestination(
    route = "settings_screen",
    icon = Icons.Default.Settings,
    name = "Settings",
    defaultTopBar = false,
    topBarContent = @Composable { TopAppBar(title = { Text("Settings") }) }
)

@Composable
fun SettingsScreen(themeChange: ()-> Unit){
    val context = LocalContext.current as MainActivity
    val sharedPref = context.getPreferences(Context.MODE_PRIVATE)
    val nickPref = stringResource(R.string.player_nick)
    val defaultNick = stringResource(id = R.string.player_default)

    val nick = remember { mutableStateOf(sharedPref.getString(nickPref, defaultNick)!!) }
    val changeNickDialog = remember { mutableStateOf(false) }
    val theme = MainActivity.gameTheme.observeAsState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)){
        Text("Your nick")

        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically){
            Box(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.6f)){
                Text(nick.value)
            }
            TextButton(onClick = { changeNickDialog.value = true }) {
                Text("Change nick")
            }
        }
        if(changeNickDialog.value){
            ChangeNickDialog(
                nickValue = nick.value,
                onDismiss = { changeNickDialog.value = false },
                onConfirm = {
                    sharedPref.edit().putString(nickPref, it).apply()
                    nick.value = it
                    changeNickDialog.value = false
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Theme")
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            LittleBoard(modifier = Modifier.padding(16.dp).size(70.dp),theme = theme.value!!)
            TextButton(onClick = { themeChange() }) {
                Text("Change theme")
            }
        }
    }
}

@Composable
fun ChangeNickDialog(nickValue: String,
                     onDismiss: ()-> Unit = {}, onConfirm: (String)->Unit) {
    Dialog(onDismissRequest = { onDismiss() }){
        Column(Modifier.clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)){
            Text("Change nick")
            val nick = remember {mutableStateOf(nickValue)}
            TextField(value = nick.value, onValueChange = {nick.value = it} )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                TextButton(onClick = { onDismiss() }) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(16.dp))
                TextButton(onClick = { onConfirm(nick.value) }){
                    Text("Confirm")
                }
            }
        }
    }
}
