package com.skryg.checkersbluetooth.ui.screens

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.R
import com.skryg.checkersbluetooth.bluetooth.BluetoothGameService
import com.skryg.checkersbluetooth.bluetooth.BluetoothUtils
import com.skryg.checkersbluetooth.bluetooth.ServiceConnectionManager
import com.skryg.checkersbluetooth.ui.NavigationDestination

object MainDestination: NavigationDestination(
    route = "main_screen",
    icon = Icons.Default.Home,
    defaultTopBar = false,
    name = "Main"
)

@Composable
fun MainScreen(localGame: () -> Unit={}, bluetoothHost: () -> Unit={}, bluetoothScan: () -> Unit = {}, navigateGame: (Long) -> Unit = {}) {
    fun isBluetoothAvailable(context: Context): Boolean {
        val bluetoothAdapter: BluetoothAdapter? =
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
        return bluetoothAdapter != null
    }

    val context = LocalContext.current
    val bluetoothAvailable = remember {
        isBluetoothAvailable(context)
    }


    val permissions = BluetoothUtils.requiredPermissions()
    var active by remember { mutableStateOf(false) }
    val bluetoothPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        if (granted) {
            active = true
        }
    }

    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween){
        Row(
            Modifier.padding(16.dp)){
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End){
                Text("Checkers", fontWeight = FontWeight.Bold, fontSize = 45.sp)
                Text("Bluetooth", fontWeight = FontWeight.Bold, fontSize = 30.sp)

            }
            val image = painterResource(id = R.drawable.checkers_logo)
            Image(modifier = Modifier
                .size(130.dp)
                .clip(RoundedCornerShape(15.dp))
                .shadow(30.dp, RoundedCornerShape(15.dp)),
                painter = image,
                contentDescription = "CheckersBT logo")
        }
        if(active) {
            BluetoothDialog(
                onDismiss = { active = false },
                bluetoothHost = bluetoothHost,
                bluetoothScan = bluetoothScan
            )
        }

        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally){
            TextButton(enabled = bluetoothAvailable, onClick = {
                try {
                    if(BluetoothGameService.isServiceRunning && BluetoothUtils.checkGranted(context.applicationContext, permissions)) {
                        val connectionManager = ServiceConnectionManager(context.applicationContext)
                        connectionManager.bindService { service ->
                            val provider = service.getProvider()
                            provider?.let {
                                navigateGame(provider.id)
                            }
                        }

                        return@TextButton
                    }
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }

                if(BluetoothUtils.checkGranted(context.applicationContext, permissions)) {
                    active = true
                } else {
                    bluetoothPermissionsLauncher.launch(permissions)
                }
            }) {
                Text("Start Bluetooth game")
            }

            TextButton(onClick = localGame) {
                Text("Start Local game")
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothDialog(onDismiss: () -> Unit = {}, bluetoothHost: () -> Unit, bluetoothScan: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        val modifier = Modifier
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
            .alpha(0.80f)

        Column(modifier){


            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally){
                Button(onClick = bluetoothHost, modifier = Modifier.fillMaxWidth(0.8f)) {
                    Text("Host a game")
                }

                Button(onClick = bluetoothScan, modifier = Modifier.fillMaxWidth(0.8f)) {
                    Text("Join a game")
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}