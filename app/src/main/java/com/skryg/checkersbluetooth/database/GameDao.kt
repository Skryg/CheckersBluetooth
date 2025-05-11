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
    suspend fun insert(game: GameEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(move: Move): Long

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameFlow(id: Long): Flow<GameEntity>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGame(id: Long): List<GameEntity>

    @Query("SELECT * FROM games ORDER BY creationTime DESC")
    suspend fun getAllGames(): List<GameEntity>

    @Query("SELECT * FROM games " +
            "WHERE gameConnection = 'LOCAL' AND winner = 'ONGOING' " +
            "ORDER BY creationTime DESC")
    suspend fun getActiveLocalGames(): List<GameEntity>

    @Transaction
    @Query("SELECT * FROM games WHERE id = :id")
    fun getGameWithMovesFlow(id: Long): Flow<GameWithMoves>

    @Transaction
    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameWithMoves(id: Long): List<GameWithMoves>

    @Transaction
    @Query("SELECT * FROM games")
    suspend fun getAllGamesWithMoves(): List<GameWithMoves>

    @Update
    suspend fun update(game: GameEntity)
}