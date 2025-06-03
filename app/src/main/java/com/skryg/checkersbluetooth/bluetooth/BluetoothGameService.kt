package com.skryg.checkersbluetooth.bluetooth

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.R
import com.skryg.checkersbluetooth.game.logic.core.PlayerMover
import com.skryg.checkersbluetooth.game.logic.core.standard.StandardGameCoreFactory
import com.skryg.checkersbluetooth.game.logic.model.GameConnection
import com.skryg.checkersbluetooth.game.logic.model.GameType
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.logic.model.toPoint
import com.skryg.checkersbluetooth.game.services.BluetoothGameProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock

object BluetoothSocketWrapperHolder {
    var socket: BluetoothSocket? = null
}

object GameCreationCallback {
    var callback: ((Long) -> Unit)? = null
}

class BluetoothGameService: LifecycleService() {
    companion object {
        const val NOTIFICATION_ID = 101
        var isServiceRunning = false
            private set
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private var inputThread: Thread? = null
    private var connectionThread: ConnectionThread? = null
    private var gameProvider: BluetoothGameProvider? = null
    private lateinit var myName: String
    private val _gameId = MutableStateFlow<Long?> (null)
    val gameId = _gameId.asStateFlow()

    private lateinit var opponentName: String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            createNotification(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            } else {
                0
            })
        application.getSharedPreferences(CheckersApplication.TAG, MODE_PRIVATE).let { sharedPref ->
            myName = sharedPref.getString(getString(R.string.player_nick), getString(R.string.player_default))
                ?: getString(R.string.player_default)
        }

        val socket = BluetoothSocketWrapperHolder.socket

        connectionThread = socket?.let {
            bluetoothSocket = it
            ConnectionThread(it)
        }
        connectionThread?.start()

        isServiceRunning = true
        return START_STICKY
    }

    private fun createGame(
        localPlayerTurn: Turn,
        gameType: GameType = GameType.STANDARD,
        whitePlayer: String = myName,
        blackPlayer: String = opponentName
    ) = lifecycleScope.launch {
        val appContainer = (application as CheckersApplication).container
        val gameId = appContainer.gameController.createGame(
            GameConnection.BLUETOOTH, whitePlayer, blackPlayer)
        val gameFactory = if(gameType == GameType.STANDARD)
                StandardGameCoreFactory(appContainer.gameRepository)
            else
                StandardGameCoreFactory(appContainer.gameRepository)


        gameProvider = BluetoothGameProvider(
            gameId,
            this@BluetoothGameService,
            gameFactory,
            localPlayerTurn,
            lifecycleScope
        )
        appContainer.gameController.loadGame(gameProvider!!)
        _gameId.value = gameId

    }

    fun getProvider(): BluetoothGameProvider? {
        return gameProvider
    }

    fun handleMessage(message: Message) {
        when (message) {
            is GameInitMessage -> {
                Log.d("GameService", "Game initialized with type: ${message.gameType} and player: ${message.playerName}")

                val initMsg: GameInitMessage = message
                val myTurn = if (initMsg.localPlayerTurn == Turn.WHITE) Turn.BLACK else Turn.WHITE
                opponentName = initMsg.playerName

                val white = if (myTurn == Turn.WHITE) myName else opponentName
                val black = if (myTurn == Turn.BLACK) myName else opponentName
                createGame(myTurn, initMsg.gameType, white, black)

                val initAck = GameInitAckMessage(GameType.STANDARD, myTurn, myName)
                sendMessage(initAck)
            }
            is GameInitAckMessage -> {
                Log.d("GameService", "Game initialization acknowledged for player: ${message.playerName}")
                val initAck: GameInitAckMessage = message
                opponentName = initAck.playerName
                val myTurn = if (initAck.localPlayerTurn == Turn.WHITE) Turn.BLACK else Turn.WHITE

                val white = if (myTurn == Turn.WHITE) myName else opponentName
                val black = if (myTurn == Turn.BLACK) myName else opponentName
                createGame(myTurn, initAck.gameType, white, black)

            }
            is MoveMessage -> {
                Log.d("GameService", "Move received from ${message.from} to ${message.to}")

                lifecycleScope.launch {
                    val moveMessage: MoveMessage = message
                    val playerMover: PlayerMover = gameProvider?.getRemotePlayerMover() ?:
                        throw IllegalStateException("Game provider is not initialized")
                    playerMover.move(moveMessage.from.toPoint(), moveMessage.to.toPoint())
                }
            }
            is DrawMessage -> {
                Log.d("GameService", "Draw request from player: ${message.playerName}")
                lifecycleScope.launch{
                    val playerMover: PlayerMover = gameProvider?.getRemotePlayerMover() ?:
                        throw IllegalStateException("Game provider is not initialized")
                    playerMover.draw()
                }

            }
            is ResignMessage -> {
                Log.d("GameService", "Resignation from player: ${message.playerName}")
                lifecycleScope.launch {
                    val playerMover: PlayerMover = gameProvider?.getRemotePlayerMover() ?:
                        throw IllegalStateException("Game provider is not initialized")
                    playerMover.resign()
                }
            }
        }
    }


    fun initializeGame(
        gameType: GameType = GameType.STANDARD,
        localPlayerTurn: Turn = Turn.WHITE
    ) {
        val initMessage = GameInitMessage(gameType, localPlayerTurn, myName)
        Log.d("GameService", "Sending game initialization message: $initMessage")
        sendMessage(initMessage)
    }

    private inner class ConnectionThread(
        private val socket: BluetoothSocket
    ) : Thread() {
        private val inputStream = socket.inputStream
        private val outputStream = socket.outputStream

        override fun run() {
            try {
                Log.d("GameService", "Starting connection thread")
                startListening(socket)
                stopSelf()
            } catch (e: IOException) {
                Log.e("GameService", "Error starting connection thread", e)
            }
        }

        private fun startListening(socket: BluetoothSocket) {
            val buffer = ByteArray(1024)
            try {
                var messageStr = ""
                while (!currentThread().isInterrupted) {
                    val bytes = inputStream.read(buffer)
                    messageStr += String(buffer, 0, bytes)
                    var leftBrackets = 0
                    var rightBrackets = 0
                    var id = 0
                    for(i in messageStr.indices) {
                        if (messageStr[i] == '{') {
                            leftBrackets++
                        } else if (messageStr[i] == '}') {
                            rightBrackets++
                        }
                        id = i
                        if( leftBrackets == rightBrackets && leftBrackets > 0) {
                            break
                        }
                    }

                    if(leftBrackets == rightBrackets) {
                        val message = messageStr.trim()
                        if (message.isNotEmpty()) {
                            Log.d("GameService", "Received complete message: $message")
                            val deserialized = MessageSerializer().deserialize(messageStr.trim())
                            handleMessage(deserialized)
                            messageStr=messageStr.drop(id+1) // Reset for next message
                        }
                    } else {
                        Log.d("GameService", "Incomplete message received, waiting for more data")
                    }

                    Log.d("GameService", "Received: $messageStr")
                    // Send to UI via broadcast or callback
                }
            } catch (e: IOException) {
                Log.e("GameService", "Error reading from socket", e)
            } finally {
                try {
                    socket.close()
                    stopSelf()
                } catch (e: IOException) {
                    Log.e("GameService", "Error closing socket", e)
                }
            }
        }
        fun sendMessage(message: Message) {
            try {
                val serializedMessage = MessageSerializer().serialize(message)
                outputStream.write(serializedMessage.toByteArray())
                outputStream.flush()
                Log.d("GameService", "Sent message: $serializedMessage")
            } catch (e: IOException) {
                Log.e("GameService", "Error sending message", e)
            }
        }

    }

    fun sendMessage(message:Message) {
        connectionThread?.sendMessage(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        inputThread?.interrupt()
        bluetoothSocket?.close()
        isServiceRunning = false
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothGameService = this@BluetoothGameService
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return LocalBinder()
    }

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


    private fun BluetoothGameProvider.getRemotePlayerMover(): PlayerMover {
        return if (gameProvider?.localPlayerTurn == Turn.WHITE) getBlackMover() else getWhiteMover()
    }

}
