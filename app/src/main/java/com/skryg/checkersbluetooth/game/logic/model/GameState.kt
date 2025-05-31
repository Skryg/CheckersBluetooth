package com.skryg.checkersbluetooth.game.logic.model

import kotlin.concurrent.Volatile


data class GameState(
    @Volatile
    var turn: Turn = Turn.WHITE,
    @Volatile
    var result: GameResult = GameResult.ONGOING,
    @Volatile
    var lastMove: Point? = null,
    @Volatile
    var gameId: Long = -1,
    val board: MutableGameBoard = MutableGameBoard(),
    var nameWhite: String = "White",
    var nameBlack: String = "Black",

    @Volatile
    var drawWhite: Boolean = false,
    @Volatile
    var drawBlack: Boolean = false,
){

    fun draw(color: Turn) {
        if (color == Turn.BLACK) {
            drawBlack = true
        } else {
            drawWhite = true
        }
    }

    fun resetDraw(){
        drawWhite = false
        drawBlack = false
    }

    fun canDraw(): Boolean {
        return drawWhite && drawBlack
    }

    fun copy(): GameStateReadonly {
        return GameStateReadonly(
            turn = turn,
            result = result,
            lastMove = lastMove,
            board = board as GameBoard,
            gameId = gameId,
            nameWhite = nameWhite,
            nameBlack = nameBlack,
            drawWhite = drawWhite,
            drawBlack = drawBlack
        )
    }
}

data class GameStateReadonly(
    val turn: Turn,
    val result: GameResult,
    val lastMove: Point?,
    val board: GameBoard,
    val gameId: Long,
    val nameWhite: String,
    val nameBlack: String,
    val drawWhite: Boolean = false,
    val drawBlack: Boolean = false
){}

enum class Turn {
    WHITE,
    BLACK
}

enum class GameResult{
    ONGOING,
    WHITE_WON,
    BLACK_WON,
    DRAW
}

enum class GameConnection{
    LOCAL,
    BLUETOOTH
}

enum class GameType {
    STANDARD
}