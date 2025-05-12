package com.skryg.checkersbluetooth.game.ui.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.services.GameController
import com.skryg.checkersbluetooth.game.ui.utils.BoardUpdater
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.ArrayList

class LocalGameViewModel(gameController: GameController, gid: Long =0) : ViewModel(), BoardUpdater {
    private val _gameUiState = MutableStateFlow(UiState())
    val gameUiState = _gameUiState.asStateFlow()

    private val provider = gameController.getGame(gid)

    init {
        println("Provider: $provider, gameId: $gid")
        println("New GameViewModel")
        if(provider != null){
            viewModelScope.launch {
                println("LAUNCH VIEWMODEL SCOPE")
                provider.getStateStreamer().getStateFlow().collect {
                    println("COLLECTING STATE FLOW")
                    _gameUiState.update { gameState ->
                        println("Updating game state")
                        val list = ArrayList<PieceUi>()

                        it.board.forEach { point, piece ->
                            if(piece != null) {
                                list.add(PieceUi(piece.color == Turn.BLACK, piece.king, point))
                            }
                        }
                        val movables = provider.getMoveChecker().getMovables()

                        return@update gameState.copy(
                            pieces = list,
                            canMove = movables,
                            turn = it.turn,
                            result = it.result
                        )
                    }

                }
            }

        }
    }

    override fun updateSelected(point: Point?) {
        val list = point?.let{provider?.getMoveChecker()?.getMoves(point)}
        _gameUiState.update{
            it.copy(movePoints = list ?: emptyList())
        }
    }

    override suspend fun move(point1: Point, point2: Point) {
        viewModelScope.launch {
            if(provider == null) return@launch
            val mover = if(provider.getStateStreamer().getStateFlow().value.turn == Turn.WHITE)
                provider.getWhiteMover() else provider.getBlackMover()
            mover.move(point1, point2)
        }
    }

    fun resign(color: Turn) {
        viewModelScope.launch {
            if(provider == null) return@launch
            val mover = if(color == Turn.WHITE)
                provider.getWhiteMover() else provider.getBlackMover()
            mover.resign()
        }
    }

    fun proposeDraw(color: Turn) {
        viewModelScope.launch {
            if(provider == null) return@launch
            val mover = if(color == Turn.WHITE)
                provider.getWhiteMover() else provider.getBlackMover()
            mover.draw()
        }
    }
}

data class PlayerState(
    val name: String = "",
    val turn: Boolean = true
)