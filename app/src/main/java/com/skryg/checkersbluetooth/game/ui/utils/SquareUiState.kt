package com.skryg.checkersbluetooth.game.ui.utils

data class UiState(
    val canMove: List<Point> = emptyList(),
    val movePoints: List<Point> = emptyList(),
    val pieces: List<PieceUi> = emptyList()
)

data class Point(
    val col: Int,
    val row: Int
)

operator fun Point.plus(other: Point) = Point(col + other.col, row + other.row)

data class PieceUi(
    val isDark: Boolean = false,
    val isKing: Boolean = false,
    val point: Point
)

