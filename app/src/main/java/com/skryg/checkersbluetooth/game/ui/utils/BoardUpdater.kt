package com.skryg.checkersbluetooth.game.ui.utils

import com.skryg.checkersbluetooth.game.logic.model.Point

interface BoardUpdater {
    fun updateSelected(point: Point?)
    suspend fun move(point1: Point, point2: Point)
}

