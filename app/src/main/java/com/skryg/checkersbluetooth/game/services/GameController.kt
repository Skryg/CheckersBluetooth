package com.skryg.checkersbluetooth.game.services

import com.skryg.checkersbluetooth.game.logic.core.GameCoreFactory
import com.skryg.checkersbluetooth.game.logic.model.GameConnection

interface GameController {
    suspend fun loadGame(gameProvider: GameProvider)
    fun getGame(id: Long): GameProvider?
    suspend fun createGame(
        gameConnection: GameConnection,
        whitePlayer: String = "",
        blackPlayer: String = ""
    ): Long
}
