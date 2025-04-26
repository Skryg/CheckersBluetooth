package com.skryg.checkersbluetooth.game.services

import com.skryg.checkersbluetooth.game.logic.core.GameCoreFactory

interface GameController {
    suspend fun createGame(gameCoreFactory: GameCoreFactory): Long
    suspend fun loadGame(gameCoreFactory: GameCoreFactory, id: Long)
    fun getGame(id: Long): GameProvider?
}
