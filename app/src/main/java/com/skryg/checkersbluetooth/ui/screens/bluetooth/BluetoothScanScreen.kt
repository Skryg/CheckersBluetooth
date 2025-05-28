package com.skryg.checkersbluetooth.ui.screens.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.ui.NavigationDestination
import kotlinx.coroutines.flow.collectLatest

object BluetoothScanDestination: NavigationDestination(
    route = "bluetooth_scan_screen",
    name = "Bluetooth Scan",
    defaultTopBar = true,
    defaultBottomBar = false
)





@Composable
fun BluetoothScanScreen(viewModel : BluetoothViewModel) {

    val devices by viewModel.devices.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.toastFlow.collectLatest { toast ->
            // Show a toast message when a new toast is emitted
            Log.i("BluetoothScanScreen", "Received toast: $toast")
            if (toast.isNotEmpty()) {
                Log.i("BluetoothScanScreen", "Toast: $toast")
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
            }
        }
    }

    BluetoothScanView(
        devices = devices,
        onDeviceClick = { device ->
            viewModel.connectToDevice(device)
        },
        onStartScan = { viewModel.scanForDevices() },
        onStopScan = { viewModel.stopScan() }
    )
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothScanView(
    devices: List<BluetoothDevice>,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
) {
    var isScanning by remember { mutableStateOf(false)}

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Nearby Games", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        Button(
            onClick = {
                if (isScanning) onStopScan() else onStartScan()
                isScanning = !isScanning
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isScanning) "Stop Scanning" else "Start Scan")
        }

        Spacer(modifier = Modifier.height(16.dp))



        LazyColumn {
            items(devices) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onDeviceClick(device) },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = device.name ?: "Unknown Device")
                        Text(text = device.address, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BluetoothScanScreenPreview() {

    BluetoothScanView(
        devices = emptyList(),
        onDeviceClick = {},
        onStartScan = {},
        onStopScan = {}
    )
}