package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.GameCoreFactory
import com.skryg.checkersbluetooth.game.logic.core.GameInitializer
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.Mover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.Turn

class StandardGameCoreFactory(repository: GameRepository?=null) : GameCoreFactory {
    private val state by lazy { GameState() }
    private val moveChecker by lazy { StandardMoveChecker(state) }
    private val initializer by lazy { StandardGameInitializer(state, moveChecker, repository) }
    private val stateStreamer by lazy { StandardStateStreamer(state, repository) }
    private val whiteMover by lazy { StandardMover(Turn.WHITE, moveChecker, state, stateStreamer, repository) }
    private val blackMover by lazy { StandardMover(Turn.BLACK, moveChecker, state, stateStreamer, repository) }

    override fun getGameInitializer(): GameInitializer {
       return initializer
    }

    override fun getMoveChecker(): MoveChecker {
        return moveChecker
    }

    override fun getWhiteMover(): Mover {
        return whiteMover
    }

    override fun getBlackMover(): Mover {
        return blackMover
    }

    override fun getStateStreamer(): StateStreamer {
        return stateStreamer
    }
}