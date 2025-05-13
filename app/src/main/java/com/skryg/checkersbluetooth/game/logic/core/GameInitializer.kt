package com.skryg.checkersbluetooth.game.logic.core

interface Initializer {
    suspend fun initialize()
}

interface GameInitializer: Initializer  {
    suspend fun load(gid: Long)
}

