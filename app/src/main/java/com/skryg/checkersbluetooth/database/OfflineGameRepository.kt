package com.skryg.checkersbluetooth.database

import kotlinx.coroutines.flow.Flow

class OfflineGameRepository(private val gameDao: GameDao): GameRepository {
    override fun getGameFlow(id: Long): Flow<GameEntity> = gameDao.getGameFlow(id)
    override fun getGamesWithMovesFlow(id: Long): Flow<GameWithMoves> = gameDao.getGamesWithMovesFlow(id)
    override suspend fun update(game: GameEntity) = gameDao.update(game)
    override suspend fun getGame(id: Long): GameEntity? = gameDao.getGame(id).firstOrNull()
    override suspend fun getGamesWithMoves(id: Long): GameWithMoves? = gameDao.getGamesWithMoves(id).firstOrNull()
    override suspend fun insert(game: GameEntity): Long = gameDao.insert(game)
    override suspend fun insert(move: Move): Long = gameDao.insert(move)

}