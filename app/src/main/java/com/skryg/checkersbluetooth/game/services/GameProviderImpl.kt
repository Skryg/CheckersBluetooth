package com.skryg.checkersbluetooth.game.services

import com.skryg.checkersbluetooth.game.logic.core.GameCoreFactory
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.Mover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import kotlinx.coroutines.runBlocking


class GameProviderImpl(val id: Long, private val factory: GameCoreFactory): GameProvider {
    init {
        runBlocking {
            val initializer = factory.getGameInitializer()
            initializer.initialize()
            initializer.load(id)
        }
    }

    override fun getWhiteMover(): Mover {
        return factory.getWhiteMover()
    }

    override fun getBlackMover(): Mover {
        return factory.getBlackMover()
    }

    override fun getMoveChecker(): MoveChecker {
        return factory.getMoveChecker()
    }

    override fun getStateStreamer(): StateStreamer {
        return factory.getStateStreamer()
    }


}