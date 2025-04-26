package com.skryg.checkersbluetooth.game.logic.core

interface GameInitializer {
    suspend fun initialize()
    suspend fun load(gid: Long)
}