package com.skryg.checkersbluetooth.game.logic

import com.skryg.checkersbluetooth.game.ui.utils.Point
import kotlinx.coroutines.flow.StateFlow

interface GameController {
    fun createGame(provider: GameProvider)
    fun calculateMoves(point: Point): List<Point>
    fun movablePieces(): List<Point>
    fun makeMove(from: Point, to: Point)
    fun getGameState(): StateFlow<GameState>
}
