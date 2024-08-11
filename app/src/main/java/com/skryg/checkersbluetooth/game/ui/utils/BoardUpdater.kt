package com.skryg.checkersbluetooth.game.ui.utils
interface BoardUpdater {
    fun updateSelected(point: Point?)
    fun PieceUi.moveTo(point: Point)
}

