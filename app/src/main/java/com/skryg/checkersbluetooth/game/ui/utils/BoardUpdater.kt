package com.skryg.checkersbluetooth.game.ui.utils

interface BoardUpdater {
    fun updateSelected(point: Point?)
    fun moveTo(point: Point)
}
