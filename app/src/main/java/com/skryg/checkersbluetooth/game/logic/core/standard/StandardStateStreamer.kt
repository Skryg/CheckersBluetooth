package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StandardStateStreamer(private val state: GameState, private val repository: GameRepository? = null): StateStreamer {
    private val mutableStateFlow = MutableStateFlow(state)

    private suspend fun endGame(winner: Int){
        val game = repository?.getGame(state.gameId)
        game?.copy(ended = true, winner = winner)?.let { repository?.insert(it) }
    }

    override fun getState(): StateFlow<GameState> {
        return mutableStateFlow.asStateFlow()
    }

    override fun update() {
        mutableStateFlow.tryEmit(state)
    }

    override suspend fun setWin(color: Turn) {
        if(state.result == GameResult.ONGOING) {
            state.result = if (color == Turn.BLACK)
                GameResult.BLACK_WON
            else
                GameResult.WHITE_WON
            endGame(state.result.ordinal)
        }
    }

    override suspend fun setDraw() {
        if(state.result == GameResult.ONGOING) {
            state.result = GameResult.DRAW
            endGame(state.result.ordinal)
        }
    }
}