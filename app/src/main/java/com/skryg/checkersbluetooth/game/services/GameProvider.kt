package com.skryg.checkersbluetooth.game.services

import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.PlayerMover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer

interface GameProvider {
    fun getWhiteMover(): PlayerMover
    fun getBlackMover(): PlayerMover
    fun getMoveChecker(): MoveChecker
    fun getStateStreamer(): StateStreamer

    class InvalidMoveException: Exception()
}
