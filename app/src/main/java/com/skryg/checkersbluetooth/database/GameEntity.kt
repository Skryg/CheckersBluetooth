package com.skryg.checkersbluetooth.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.skryg.checkersbluetooth.game.logic.model.GameConnection
import com.skryg.checkersbluetooth.game.logic.model.GameResult

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val whitePlayer: String = "White",
    val blackPlayer: String = "Black",
    //ongoing = 0, white=1 black = 2, draw = 3
    val winner: GameResult = GameResult.ONGOING,
    val gameConnection: GameConnection = GameConnection.LOCAL,
    val creationTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "moves")
data class Move(
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0,
    val gameId: Long = -1,
    val from: String = "",
    val to: String = "",
)

data class GameWithMoves(
    @Embedded val game: GameEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val moves: List<Move>
)