package com.skryg.checkersbluetooth.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.skryg.checkersbluetooth.game.ui.theme.GameTheme
import com.skryg.checkersbluetooth.game.ui.utils.Point

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val whitePlayer: String = "",
    val blackPlayer: String = "",
    val ended: Boolean = false,
    //white = 0, black = 1, draw = 2
    val winner: Int = -1
)

@Entity(tableName = "moves")
data class Move(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long = -1,
    val from: String = "",
    val to: String = "",
    val time: Long = -1,
    val king: Boolean = false
)

data class GameWithMoves(
    @Embedded val game: GameEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "gameId"
    )
    val moves: List<Move>
)