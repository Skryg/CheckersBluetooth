package com.skryg.checkersbluetooth.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val whitePlayer: String = "White",
    val blackPlayer: String = "Black",
    val ended: Boolean = false,
    //white = 0, black = 1, draw = 2
    val winner: Int = -1,
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