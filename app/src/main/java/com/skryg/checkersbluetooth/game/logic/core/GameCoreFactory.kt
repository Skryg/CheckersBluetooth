package com.skryg.checkersbluetooth.game.logic.core

interface GameCoreFactory {
    fun getGameInitializer(): GameInitializer
    fun getMoveChecker(): MoveChecker
    fun getStateStreamer(): StateStreamer
    fun getWhiteMover(): PlayerMover
    fun getBlackMover(): PlayerMover
}