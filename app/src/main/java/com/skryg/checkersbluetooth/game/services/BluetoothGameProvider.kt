package com.skryg.checkersbluetooth.game.services

import android.util.Log
import androidx.compose.runtime.collectAsState
import com.skryg.checkersbluetooth.bluetooth.BluetoothGameService
import com.skryg.checkersbluetooth.bluetooth.DrawMessage
import com.skryg.checkersbluetooth.bluetooth.MoveMessage
import com.skryg.checkersbluetooth.bluetooth.ResignMessage
import com.skryg.checkersbluetooth.game.logic.core.GameCoreFactory
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.PlayerMover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BluetoothGameProvider(
    override val id: Long,
    private var service: BluetoothGameService?,
    private val gameCoreFactory: GameCoreFactory,
    val localPlayerTurn: Turn,
) : GameProvider {
    init {
        runBlocking {
            val initializer = gameCoreFactory.getGameInitializer()
            initializer.initialize()
            initializer.load(id)
        }
    }

    fun releaseService(){
        service = null
    }

    override fun getWhiteMover(): PlayerMover {
        return gameCoreFactory.getWhiteMover()
    }

    override fun getBlackMover(): PlayerMover {
        return gameCoreFactory.getBlackMover()
    }

    fun getLocalPlayerMover(): PlayerMover {
        return BluetoothPlayerMoverWrapper(
            if(localPlayerTurn == Turn.WHITE) getWhiteMover() else getBlackMover(),
            getStateStreamer()
        )
    }

    override fun getMoveChecker(): MoveChecker {
        return BluetoothMoveCheckerWrapper(
            gameCoreFactory.getMoveChecker(),
            getStateStreamer()
        )
    }

    override fun getStateStreamer(): StateStreamer {

        return gameCoreFactory.getStateStreamer()
    }

    inner class BluetoothMoveCheckerWrapper(private val moveChecker: MoveChecker,
                                            private val streamer: StateStreamer): MoveChecker {
        override fun getMovables(): List<Point> {
            if(streamer.getStateFlow().value.turn != localPlayerTurn) {
                return emptyList()
            }
            return moveChecker.getMovables()
        }

        override fun getMoves(point: Point): List<Point> {
            if(streamer.getStateFlow().value.turn != localPlayerTurn) {
                return emptyList()
            }
            return moveChecker.getMoves(point)
        }
    }

    inner class BluetoothPlayerMoverWrapper(private val playerMover: PlayerMover,
                                            private val streamer: StateStreamer): PlayerMover {
        override suspend fun resign() {
            service?.sendMessage(
                ResignMessage("")
            )
            playerMover.resign()
        }

        override suspend fun draw() {
            service?.sendMessage(
                DrawMessage("")
            )
            playerMover.draw()
        }

        override suspend fun move(p1: Point, p2: Point): Boolean {
            if(streamer.getStateFlow().value.turn == localPlayerTurn) {
                service?.sendMessage(MoveMessage(
                    from = p1.toString(),
                    to = p2.toString()
                ))
                return playerMover.move(p1, p2)
            }
            return false
        }
    }
}