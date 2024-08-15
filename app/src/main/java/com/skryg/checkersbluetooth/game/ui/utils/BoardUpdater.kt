package com.skryg.checkersbluetooth.game.ui.utils
interface BoardUpdater {
    fun updateSelected(point: Point?)
    fun move(point1: Point, point2: Point)
}

