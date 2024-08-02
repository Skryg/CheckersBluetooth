package com.skryg.checkersbluetooth.game.ui.utils

data class SquareUiState(
    val isSelected: Boolean = false,
    val isDarkSquare: Boolean = false,
    val isMoveAvailable: Boolean = false,
    val canMoveTo: Boolean = false,
    val piece: PieceUi? = null
)

data class PieceUi(
    val isDark: Boolean = false,
    val isKing: Boolean = false
)

