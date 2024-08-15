package com.skryg.checkersbluetooth.game.logic

import com.skryg.checkersbluetooth.game.ui.utils.Point
import kotlinx.coroutines.flow.StateFlow

interface GameProvider {
    fun gameStateFlow(): StateFlow<GameState>
    fun makeMove(from: Point, to: Point)
    fun calculateMoves(point: Point): List<Point>
    fun getMovables(): List<Point>

    class InvalidMoveException: Exception()
}
