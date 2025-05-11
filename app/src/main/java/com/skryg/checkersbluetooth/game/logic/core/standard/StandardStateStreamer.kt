package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.GameStateReadonly
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StandardStateStreamer(private val state: GameState,
                            private val repository: GameRepository? = null)
    : StateStreamer {
    private val mutableStateFlow = MutableStateFlow(state.copy())

    private suspend fun endGame(winner: GameResult){
        val game = repository?.getGame(state.gameId)
        game?.copy(winner = winner)?.let { repository?.insert(it) }
    }

    override fun getStateFlow(): StateFlow<GameStateReadonly> {
        return mutableStateFlow.asStateFlow()
    }

    override fun update() {
        println("Updating state streamer")
        mutableStateFlow.update {
            it.copy(
                turn = state.turn,
                result = state.result,
                lastMove = state.lastMove,
                board = state.board,
                gameId = state.gameId,
                nameWhite = state.nameWhite,
                nameBlack = state.nameBlack
            )
        }
    }

    override suspend fun setWin(color: Turn) {
        if(state.result == GameResult.ONGOING) {
            state.result = if (color == Turn.BLACK)
                GameResult.BLACK_WON
            else
                GameResult.WHITE_WON
            endGame(state.result)

            update()
        }
    }

    override suspend fun setDraw() {
        if(state.result == GameResult.ONGOING) {
            state.result = GameResult.DRAW
            endGame(state.result)

            update()
        }
    }
}