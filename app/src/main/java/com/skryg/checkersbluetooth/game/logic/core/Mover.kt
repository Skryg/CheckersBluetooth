package com.skryg.checkersbluetooth.game.logic.core

import com.skryg.checkersbluetooth.game.logic.model.Point

interface Mover {
    suspend fun move(p1: Point, p2: Point): Boolean

}

interface PlayerMover : Mover {
    suspend fun resign()
    suspend fun draw()
}

abstract class  MoveStage {
    var list: List<MoveStage> = emptyList()

    abstract suspend fun handle(p1: Point, p2: Point): Boolean
    fun setNext(stages: List<MoveStage>){
        list = stages
    }
}
