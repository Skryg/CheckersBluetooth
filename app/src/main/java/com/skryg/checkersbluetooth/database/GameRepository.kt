package com.skryg.checkersbluetooth.database

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun insert(game: GameEntity): Long
    suspend fun insert(move: Move): Long
    suspend fun update(game: GameEntity)
    suspend fun getGame(id: Long): GameEntity?
    suspend fun getGamesWithMoves(id: Long): GameWithMoves?
    fun getGameFlow(id: Long): Flow<GameEntity>
    fun getGamesWithMovesFlow(id: Long): Flow<GameWithMoves>
}