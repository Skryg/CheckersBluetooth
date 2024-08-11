package com.skryg.checkersbluetooth.game

import com.skryg.checkersbluetooth.game.ui.utils.PieceUi

data class GameState(
    val white: String = "White Player",
    val black: String = "Black Player",
    val turn: Boolean = false,
    val pieces: List<PieceUi> = emptyList(),
    val result: GameResult = GameResult.ONGOING
)

enum class GameResult{
    WHITE_WON,
    BLACK_WON,
    DRAW,
    ONGOING
}