package com.skryg.checkersbluetooth.database

import kotlinx.coroutines.flow.Flow

class OfflineGameRepository(private val gameDao: GameDao): GameRepository {
    override fun getGameFlow(id: Long): Flow<GameEntity> = gameDao.getGameFlow(id)
    override fun getGamesWithMovesFlow(id: Long): Flow<GameWithMoves> = gameDao.getGameWithMovesFlow(id)
    override suspend fun update(game: GameEntity) = gameDao.update(game)
    override suspend fun getGame(id: Long): GameEntity? = gameDao.getGame(id).firstOrNull()
    override suspend fun getGameWithMoves(id: Long): GameWithMoves? = gameDao.getGameWithMoves(id).firstOrNull()
    override suspend fun getActiveLocalGames(): List<GameEntity> = gameDao.getActiveLocalGames()
    override suspend fun getAllGames(): List<GameEntity> = gameDao.getAllGames()
    override suspend fun getAllGamesWithMoves(): List<GameWithMoves> = getAllGamesWithMoves()

    override suspend fun insert(game: GameEntity): Long = gameDao.insert(game)
    override suspend fun insert(move: Move): Long = gameDao.insert(move)

}