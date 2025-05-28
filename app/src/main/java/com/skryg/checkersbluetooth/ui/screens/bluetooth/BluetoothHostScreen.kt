package com.skryg.checkersbluetooth.ui.screens.bluetooth

//import android.bluetooth.BluetoothDevice
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.ui.utils.LittleBoard
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.ui.NavigationDestination
import com.skryg.checkersbluetooth.ui.utils.OnceButton

@OptIn(ExperimentalMaterial3Api::class)
object BluetoothHostDestination: NavigationDestination(
    route = "bluetooth_host_screen",
    name = "Bluetooth Host",
    defaultTopBar = false,
    defaultBottomBar = false,
    topBarContent = @Composable { TopAppBar(title = { Text("Bluetooth Hosting") }) }

)

@Composable
fun BluetoothHostScreen(viewModel : BluetoothViewModel){
//    val connectedDevice = remember { mutableStateOf<BluetoothDevice?>(null) }

    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {}
    val durationSecs = 300


    BluetoothHostView(
        onCancelHosting = { viewModel.cancelHosting() },
        onPlay = {
            viewModel.startHosting()
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, durationSecs)
            }
            discoverableLauncher.launch(intent)
        }
    )
}

@Composable
fun BluetoothHostView(
    onCancelHosting: () -> Unit,
    onPlay: () -> Unit
) {
    var black by remember { mutableStateOf(false) }
    val pieces = listOf(
        PieceUi(point= Point(0,1), isDark = black),
        PieceUi(point= Point(1,0), isDark = black)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {

        var hostingEnabled by remember { mutableStateOf(false) }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(6.dp)
            ) {
                LittleBoard(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    pieceList = pieces
                )

                Switch(
                    checked = black,
                    onCheckedChange = { black = it },
                )

            }
        }
        Column {
            OnceButton(
                onClick = {
                    onPlay()
                    hostingEnabled = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Play")
            }
        }

        if(hostingEnabled) {
            BluetoothHostDialog {
                onCancelHosting()
                hostingEnabled = false
            }
        }

    }
}

@Composable
fun BluetoothHostDialog(onCancelHosting: () -> Unit) {
    Dialog(onDismissRequest = onCancelHosting) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
                .alpha(0.95f)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Waiting for player", fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            OnceButton(onClick = onCancelHosting) {
                Text("Cancel")
            }
        }
    }
}

@Preview
@Composable
fun BluetoothHostScreenPreview() {
    BluetoothHostView(
        onCancelHosting = {},
        onPlay = {}
    )
}