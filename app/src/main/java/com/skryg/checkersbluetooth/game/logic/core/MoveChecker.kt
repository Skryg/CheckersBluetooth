package com.skryg.checkersbluetooth.game.logic.core

import com.skryg.checkersbluetooth.game.logic.model.Point

interface MoveChecker {
    fun getMovables(): List<Point>
    fun getMoves(point: Point): List<Point>
}