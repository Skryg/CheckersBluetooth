package com.skryg.checkersbluetooth.game.services

import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.exception.GameEndedException
import com.skryg.checkersbluetooth.game.logic.exception.GameLoadException
import com.skryg.checkersbluetooth.game.logic.exception.GameNotFoundException
import com.skryg.checkersbluetooth.game.logic.model.GameConnection
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import java.util.concurrent.ConcurrentHashMap


class GameControllerImpl(private val repository: GameRepository? = null): GameController {
    private val games = ConcurrentHashMap<Long, GameProvider>()

    private fun randomGID() = kotlin.random.Random(System.currentTimeMillis()).nextLong()

    //returns game ID
    override suspend fun createGame(gameConnection: GameConnection, whitePlayer:String, blackPlayer: String): Long {
        var gid = randomGID()
        if(repository != null) {
            val game = GameEntity(whitePlayer = whitePlayer, blackPlayer = blackPlayer)
            gid = repository.insert(game)
        }

        return gid
    }

    override suspend fun loadGame(gameProvider: GameProvider) {
        val gameEntity = repository?.getGame(gameProvider.id)
            ?: throw GameLoadException("Game couldn't be loaded", GameNotFoundException())

        if (gameEntity.winner != GameResult.ONGOING)
            throw GameLoadException("Game couldn't be loaded", GameEndedException())

        games[gameProvider.id] = gameProvider
    }

    override fun getGame(id: Long): GameProvider? {
        return games[id]
    }
}