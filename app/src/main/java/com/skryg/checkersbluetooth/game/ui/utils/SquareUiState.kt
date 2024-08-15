package com.skryg.checkersbluetooth.game.ui.utils

data class UiState(
    val canMove: List<Point> = emptyList(),
    val movePoints: List<Point> = emptyList(),
    val pieces: List<PieceUi> = emptyList()
)

data class Point(
    val col: Int,
    val row: Int
) {
    operator fun minus(other: Point) = Point(col - other.col, row - other.row)
    operator fun plus(other: Point) = Point(col + other.col, row + other.row)
    operator fun div(num: Int) = Point(col/num, row/num)
}

data class PieceUi(
    val isDark: Boolean = false,
    val isKing: Boolean = false,
    val point: Point
)

