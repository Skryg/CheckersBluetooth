package com.skryg.checkersbluetooth

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.SoundPool
import android.util.Log
import com.skryg.checkersbluetooth.database.AppContainer
import com.skryg.checkersbluetooth.database.AppDataContainer
import com.skryg.checkersbluetooth.sound.GameSounds
import com.skryg.checkersbluetooth.sound.GameSoundsImpl
import com.skryg.checkersbluetooth.ui.screens.bluetooth.parcelable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CheckersApplication: Application() {
    companion object {
        const val TAG = "CheckersApplication"
    }

    lateinit var container: AppContainer
    lateinit var gameSounds: GameSounds
    private val _bluetoothDevices = MutableStateFlow<Set<BluetoothDevice>>(emptySet())
    val bluetoothDevices = _bluetoothDevices.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        gameSounds = GameSoundsImpl(this)
    }

    @SuppressLint("MissingPermission")
    fun addDevice(device: BluetoothDevice) {
        Log.d(TAG, "Adding device: ${device.name} - ${device.address}")
        _bluetoothDevices.value += device
    }


}
