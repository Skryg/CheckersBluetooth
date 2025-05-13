package com.skryg.checkersbluetooth.game.logic.core

interface GameCoreFactory {
    fun getGameInitializer(): GameInitializer
    fun getMoveChecker(): MoveChecker
    fun getStateStreamer(): StateStreamer
    fun getWhiteMover(): PlayerMover
    fun getBlackMover(): PlayerMover
}

interface SpectateGameFactory {
    fun getSpectateMover(): ReversibleMover
    fun getSpectateStateStreamer(): BaseStateStreamer
    fun getBoardInitializer(): Initializer
}