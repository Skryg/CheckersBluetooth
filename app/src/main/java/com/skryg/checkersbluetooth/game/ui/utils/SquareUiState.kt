package com.skryg.checkersbluetooth.game.ui.utils

import com.skryg.checkersbluetooth.game.logic.model.GameBoard
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn

data class UiState(
    val canMove: List<Point> = emptyList(),
    val movePoints: List<Point> = emptyList(),
    val pieces: List<PieceUi> = emptyList(),
    val turn: Turn = Turn.WHITE
)


data class PieceUi(
    val isDark: Boolean = false,
    val isKing: Boolean = false,
    val point: Point
)

