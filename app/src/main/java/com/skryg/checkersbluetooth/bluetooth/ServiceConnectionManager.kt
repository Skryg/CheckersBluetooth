package com.skryg.checkersbluetooth.bluetooth

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import java.util.logging.Logger

class ServiceConnectionManager(private val context: Context) {
    private var serviceInstance: BluetoothGameService? = null
    private var isBound = false
    private var onConnected: (BluetoothGameService) -> Unit = {}
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service == null) {
                throw IllegalStateException("Service is null")
            }
            serviceInstance = (service as BluetoothGameService.LocalBinder).getService()
            isBound = true

            try {
                serviceInstance?.let { onConnected(it) }
            } catch (e: Exception) {
                Logger.getLogger("ServiceConnectionManager").severe("Error in onConnected: ${e.message}")
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            serviceInstance = null
        }
    }
    fun bindService(onConnected: (BluetoothGameService) -> Unit = {}) {
        this.onConnected = onConnected
        val intent = Intent(context, BluetoothGameService::class.java)
        context.bindService(intent, serviceConnection, 0)
    }

    fun unbindService() {
        if(isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }
}