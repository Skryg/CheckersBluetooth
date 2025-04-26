package com.skryg.checkersbluetooth.game.services

import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.Mover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer

interface GameProvider {
    fun getWhiteMover(): Mover
    fun getBlackMover(): Mover
    fun getMoveChecker(): MoveChecker
    fun getStateStreamer(): StateStreamer

    class InvalidMoveException: Exception()
}
