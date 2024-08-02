package com.skryg.checkersbluetooth.game.ui.theme

import androidx.compose.ui.graphics.Color

data class GameTheme(
    val backgroundColor: Color,
    val darkSquareColor: Color,
    val lightSquareColor: Color,
    val darkPieceColor: Color,
    val lightPieceColor: Color,
) {
    companion object {
        val Default = GameTheme(
            backgroundColor = Color(0xFFD0BCFF),
            darkSquareColor = Color(0xFF000000),
            lightSquareColor = Color(0xFFFFFFFF),
            darkPieceColor = Color(0xFF000000),
            lightPieceColor = Color(0xFFFFFFFF),
        )
    }
}