package com.skryg.checkersbluetooth.ui.screens.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.skryg.checkersbluetooth.bluetooth.BluetoothGameService
import com.skryg.checkersbluetooth.bluetooth.BluetoothSocketWrapperHolder
import com.skryg.checkersbluetooth.bluetooth.BluetoothUtils
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.logging.Logger

@SuppressLint("MissingPermission")
class BluetoothViewModel(application: Application): AndroidViewModel(application) {
    private var initialTurn = Turn.WHITE
    private val bluetoothManager: BluetoothManager = getApplication<Application>()
        .getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    private val _devices = MutableStateFlow<List<BluetoothDevice>>(
        bluetoothAdapter?.bondedDevices?.toList() ?: emptyList())
    val devices = _devices.asStateFlow()

    private val _toastFlow = MutableSharedFlow<String>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val toastFlow = _toastFlow.asSharedFlow()

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled ?: false
    private val isBluetoothSupported: Boolean
        get() = bluetoothAdapter != null

    private val foundDevices = mutableSetOf<String>()
    private var scanning = false

    private var acceptThread: AcceptThread? = null

    private val scanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d("BluetoothViewModel", "Device found")
                    val device: BluetoothDevice? = intent.parcelable(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        Log.d("BluetoothViewModel", "Found device: ${it.name} at ${it.address}")
                        if (!foundDevices.contains(it.address)) {
                            foundDevices.add(it.address)
                            _devices.value += it
                        }
                    }
                }
            }
        }
    }

    init {
        Logger.getLogger("BluetoothViewModel")
            .info("BluetoothManager initialized: $bluetoothManager")
        Logger.getLogger("BluetoothViewModel")
            .info("Bluetooth supported: $isBluetoothSupported, enabled: $isBluetoothEnabled")
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (isBluetoothEnabled) {
            val connectThread = ConnectThread(device) {
                startGameService(it)
            }
            connectThread.start()

            Logger.getLogger("BluetoothViewModel").info("Connecting to device: ${device.name} at ${device.address}")
        } else {
            Logger.getLogger("BluetoothViewModel").warning("Bluetooth is not enabled, cannot connect to device")
        }
    }

    fun startHosting() {
        if (isBluetoothEnabled) {
            val acceptThread = AcceptThread {
                startGameService(it)
            }
            acceptThread.start()
            Logger.getLogger("BluetoothViewModel").info("Hosting started")
        } else {
            Logger.getLogger("BluetoothViewModel").warning("Bluetooth is not enabled, cannot start hosting")
        }
    }

    fun cancelHosting() {
        acceptThread?.cancel()
        acceptThread = null
        Logger.getLogger("BluetoothViewModel").info("Hosting cancelled")
    }

    fun scanForDevices() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

        val context = getApplication<Application>()
            .applicationContext
        context.registerReceiver(scanReceiver, filter)


        if (isBluetoothEnabled) {
            bluetoothAdapter?.startDiscovery()
            scanning = true
        } else {
            // Handle the case where Bluetooth is not enabled
        }
    }

    fun stopScan() {
        val context = getApplication<Application>()
            .applicationContext
        bluetoothAdapter?.cancelDiscovery()
        context.unregisterReceiver(scanReceiver)
        scanning = false
    }

    override fun onCleared() {
        super.onCleared()
        if(scanning) {
            stopScan()
            cancelHosting()
        }
    }

    fun startGameService(socket: BluetoothSocket) {
        if (isBluetoothEnabled) {
            BluetoothSocketWrapperHolder.socket = socket
            val context = getApplication<Application>().applicationContext
            val serviceIntent = Intent(context, BluetoothGameService::class.java)
            context.startForegroundService(serviceIntent)
            Logger.getLogger("BluetoothViewModel").info("Game service started")
        } else {
            Logger.getLogger("BluetoothViewModel").warning("Bluetooth is not enabled, cannot start game service")
        }
    }

    fun setTurn(turn: Turn) {
        initialTurn = turn
    }

    private inner class AcceptThread(private val onConnected: (BluetoothSocket) -> Unit): Thread() {
        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingRfcommWithServiceRecord(
                BluetoothUtils.NAME,
                BluetoothUtils.MY_UUID
            )
        }
        override fun run() {
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: Exception) {
                    Logger.getLogger("BluetoothViewModel").severe("Error accepting connection: ${e.message}")
                    shouldLoop = false
                    null
                }

                socket?.also {
                    mmServerSocket?.close()
                    shouldLoop = false
                    onConnected(it)
                }
            }
        }

        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: Exception) {
                Logger.getLogger("BluetoothViewModel").severe("Error closing server socket: ${e.message}")
            }
        }
    }

    private inner class ConnectThread(private val device: BluetoothDevice,
                                      private val onConnected: (BluetoothSocket) -> Unit) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            try {
                device.createRfcommSocketToServiceRecord(BluetoothUtils.MY_UUID)
            } catch (e: Exception) {
                Logger.getLogger("BluetoothViewModel").severe("Error creating socket: ${e.message}")
                null
            }
        }

        override fun run() {
            mmSocket?.let { socket ->
                bluetoothAdapter?.cancelDiscovery()
                try {
                    socket.connect()

                    Logger.getLogger("BluetoothViewModel").info("Connected to ${device.name} at ${device.address}")
                    onConnected(socket)
                } catch (e: Exception) {
                    Logger.getLogger("BluetoothViewModel").info("try emit ${_toastFlow.tryEmit("Error connecting to ${device.name}")}")

                    Logger.getLogger("BluetoothViewModel").severe("Error connecting to device: ${e.message}")
                    try {
                        socket.close()
                    } catch (closeException: Exception) {
                        Logger.getLogger("BluetoothViewModel").severe("Error closing socket: ${closeException.message}")
                    }
                }
            }
        }
    }

}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

