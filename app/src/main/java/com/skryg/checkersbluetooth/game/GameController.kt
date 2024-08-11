package com.skryg.checkersbluetooth.game

import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface GameController {
    fun calculateMoves(point: Point): List<Point>
    fun movablePieces(): List<Point>
    fun makeMove(from: Point, to: Point)
    fun getGameState(): Flow<GameState>
}
