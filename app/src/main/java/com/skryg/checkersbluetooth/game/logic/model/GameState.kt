package com.skryg.checkersbluetooth.game.logic.model

import kotlin.concurrent.Volatile


data class GameState(
    @Volatile
    var turn: Turn = Turn.WHITE,
    @Volatile
    var result: GameResult = GameResult.ONGOING,
    @Volatile
    var lastMove: Point? = null,
    val board: MutableGameBoard = MutableGameBoard(),
    val gameId: Long = -1,
    val nameWhite: String = "White",
    val nameBlack: String = "Black",

    @Volatile
    private var drawWhite: Boolean = false,
    @Volatile
    private var drawBlack: Boolean = false,
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
            nameBlack = nameBlack
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
    val nameBlack: String
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