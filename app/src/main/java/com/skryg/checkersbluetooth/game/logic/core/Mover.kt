package com.skryg.checkersbluetooth.game.logic.core

import com.skryg.checkersbluetooth.game.logic.model.Point

interface Mover {
    suspend fun move(p1: Point, p2: Point): Boolean
    suspend fun resign()
    suspend fun draw()
}