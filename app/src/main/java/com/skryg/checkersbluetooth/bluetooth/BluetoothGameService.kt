package com.skryg.checkersbluetooth.bluetooth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.skryg.checkersbluetooth.R
import java.io.IOException

object BluetoothSocketWrapperHolder {
    var socket: BluetoothSocket? = null
}

class BluetoothGameService: Service() {
    companion object {
        const val NOTIFICATION_ID = 101
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private var inputThread: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())

        val socket = BluetoothSocketWrapperHolder.socket
        socket?.let {
            bluetoothSocket = it
            startListening(it)
        }

        return START_STICKY
    }

    private fun startListening(socket: BluetoothSocket) {
        inputThread = Thread {
            val input = socket.inputStream
            val buffer = ByteArray(1024)
            try {
                while (!Thread.currentThread().isInterrupted) {
                    val bytes = input.read(buffer)
                    val message = String(buffer, 0, bytes)
                    Log.d("GameService", "Received: $message")
                    // Send to UI via broadcast or callback
                }
            } catch (e: IOException) {
                stopSelf()
            }
        }.apply { start() }
    }

    fun sendMove(move: String) {
        bluetoothSocket?.outputStream?.write(move.toByteArray())
    }

    override fun onDestroy() {
        super.onDestroy()
        inputThread?.interrupt()
        bluetoothSocket?.close()
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothGameService = this@BluetoothGameService
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    private fun createNotification(): Notification {
        val channelId = "bluetooth_checkers"
        val channelName = "Bluetooth Checkers Game"
        val manager = getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            channelId, channelName, NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Checkers Game")
            .setContentText("Bluetooth connection is active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

}