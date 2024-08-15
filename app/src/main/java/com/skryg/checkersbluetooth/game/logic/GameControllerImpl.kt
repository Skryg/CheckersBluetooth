package com.skryg.checkersbluetooth.game.logic

import com.skryg.checkersbluetooth.game.ui.utils.Point
import kotlinx.coroutines.flow.StateFlow

class GameControllerImpl: GameController {
    private var gameProvider: GameProvider? = null

    override fun createGame(provider: GameProvider) {
        gameProvider = provider
    }

    override fun calculateMoves(point: Point): List<Point> = gameProvider!!.calculateMoves(point)

    override fun movablePieces(): List<Point> = gameProvider!!.getMovables()

    override fun makeMove(from: Point, to: Point) = gameProvider!!.makeMove(from, to)

    override fun getGameState(): StateFlow<GameState> = gameProvider!!.gameStateFlow()
}