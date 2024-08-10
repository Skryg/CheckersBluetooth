package com.skryg.checkersbluetooth.database

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun insert(game: GameEntity)
    suspend fun insert(move: Move)
    suspend fun update(game: GameEntity)
    fun getGame(id: Long): Flow<GameEntity>
    fun getGamesWithMoves(id: Long): Flow<GameWithMoves>
}