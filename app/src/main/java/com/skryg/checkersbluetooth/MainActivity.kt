package com.skryg.checkersbluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import com.skryg.checkersbluetooth.game.ui.theme.GameTheme
import com.skryg.checkersbluetooth.ui.Navigation
import com.skryg.checkersbluetooth.ui.screens.bluetooth.parcelable
import com.skryg.checkersbluetooth.ui.theme.CheckersBluetoothTheme

class MainActivity : ComponentActivity() {
    companion object{
        var gameTheme = MutableLiveData(GameTheme.Default)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        val sharedPrefs = this.getPreferences(Context.MODE_PRIVATE)
        val themeStr = getString(R.string.game_theme)
        GameTheme.entries[sharedPrefs.getInt(themeStr,0)].let {
            gameTheme.value = it
        }
        gameTheme.observe(this){
            sharedPrefs.edit().putInt(themeStr,it.ordinal).apply()
        }

        setContent {
            CheckersBluetoothTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d("BluetoothViewModel", "Device found")
                    val device: BluetoothDevice? = intent.parcelable(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        Log.d("CheckersApplication", "Found device: ${it.name} at ${it.address}")
                        val app = (context?.applicationContext as CheckersApplication)
                        app.addDevice(it)
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheckersBluetoothTheme {
        Navigation()
    }
}