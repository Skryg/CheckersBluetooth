package com.skryg.checkersbluetooth.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(move: Move)

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGame(id: Long): Flow<GameEntity>

    @Transaction
    @Query("SELECT * FROM games WHERE id = :id")
    fun getGamesWithMoves(id: Long): Flow<GameWithMoves>

    @Update
    suspend fun update(game: GameEntity)
}