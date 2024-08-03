package com.skryg.checkersbluetooth.game.ui.theme

import androidx.compose.ui.graphics.Color

enum class GameTheme(
    val backgroundColor: Color,
    val darkSquareColor: Color,
    val lightSquareColor: Color,
    val darkPieceColor: Color,
    val lightPieceColor: Color,
) {
    Default (
        backgroundColor = Color(0xFFA2847B),
        darkSquareColor = Color(0xFF79412B),
        lightSquareColor = Color(0xFFF3D9B1),
        darkPieceColor = Color(0xFF1B1818),
        lightPieceColor = Color(0xFFDBD7D7),
        ),
    Red (
        backgroundColor = Color(0xFFFF8585),
        darkSquareColor = Color(0xFF470808),
        lightSquareColor = Color(0xFFDB6464),
        darkPieceColor = Color(0xFFC40505),
        lightPieceColor = Color(0xFFE6A5A5),
    ),
    Blue(
        backgroundColor = Color(0xFF6295FF),
        darkSquareColor = Color(0xFF06163F),
        lightSquareColor = Color(0xFF81B2FA),
        darkPieceColor = Color(0xFF1D5AC0),
        lightPieceColor = Color(0xFF8893D5),
    )

}