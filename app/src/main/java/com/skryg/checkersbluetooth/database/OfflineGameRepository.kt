package com.skryg.checkersbluetooth.database

import kotlinx.coroutines.flow.Flow

class OfflineGameRepository(private val gameDao: GameDao): GameRepository {
    override fun getGame(id: Long): Flow<GameEntity> = gameDao.getGame(id)
    override fun getGamesWithMoves(id: Long): Flow<GameWithMoves> = gameDao.getGamesWithMoves(id)
    override suspend fun update(game: GameEntity) = gameDao.update(game)
    override suspend fun insert(game: GameEntity) = gameDao.insert(game)
    override suspend fun insert(move: Move) = gameDao.insert(move)

}