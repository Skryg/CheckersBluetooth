package com.skryg.checkersbluetooth.game.services

import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.GameCoreFactory
import com.skryg.checkersbluetooth.game.logic.exception.GameEndedException
import com.skryg.checkersbluetooth.game.logic.exception.GameLoadException
import com.skryg.checkersbluetooth.game.logic.exception.GameNotFoundException
import java.util.TreeMap


class GameControllerImpl(private val repository: GameRepository? = null): GameController {
    private val games = TreeMap<Long, GameProvider>()

    private fun randomGID() = kotlin.random.Random(System.currentTimeMillis()).nextLong()

    //returns game ID
    override suspend fun createGame(gameCoreFactory: GameCoreFactory): Long {
        var gid = randomGID()
        if(repository != null) {
            val game = GameEntity()
            gid = repository.insert(game)
        }
        games[gid] = GameProviderImpl(gid, gameCoreFactory)

        return gid
    }

    override suspend fun loadGame(gameCoreFactory: GameCoreFactory, id: Long) {
        val gameEntity = repository?.getGamesWithMoves(id)
            ?: throw GameLoadException("Game couldn't be loaded", GameNotFoundException())

        if (gameEntity.game.ended)
            throw GameLoadException("Game couldn't be loaded", GameEndedException())

        games[id] = GameProviderImpl(id, gameCoreFactory)
    }

    override fun getGame(id: Long): GameProvider? {
        return games[id]
    }
}