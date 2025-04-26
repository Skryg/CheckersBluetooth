package com.skryg.checkersbluetooth.game.logic.model

import kotlin.concurrent.Volatile


class GameState {
    @Volatile
    var turn: Turn = Turn.WHITE
    @Volatile
    var result: GameResult = GameResult.ONGOING
    @Volatile
    var lastMove: Point? = null
    val board: GameBoard = GameBoard()
    val gameId: Long = -1
    val nameWhite: String = "White"
    val nameBlack: String = "Black"

    @Volatile
    private var drawWhite = false
    @Volatile
    private var drawBlack = false

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
}

enum class Turn {
    WHITE,
    BLACK
}

enum class GameResult{
    WHITE_WON,
    BLACK_WON,
    DRAW,
    ONGOING
}