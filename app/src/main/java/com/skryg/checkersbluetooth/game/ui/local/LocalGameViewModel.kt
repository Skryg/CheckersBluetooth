package com.skryg.checkersbluetooth.game.ui.local

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

class LocalGameViewModel(private val gameController: GameController, private var gid: Long =0) : ViewModel(), BoardUpdater {
    private val _gameUiState = MutableStateFlow(UiState())
    val gameUiState = _gameUiState.asStateFlow()

    private val provider = gameController.getGame(gid)

    init {
        println("New GameViewModel")
        if(provider != null){
            viewModelScope.launch {

                provider.getStateStreamer().getState().collect {
                    _gameUiState.update { gameState ->
                            val list = ArrayList<PieceUi>()

                            it.board.forEach { point, piece ->
                                if(piece != null) {
                                    list.add(PieceUi(piece.color == Turn.BLACK, piece.king, point))
                                }
                            }
                            val movables = provider.getMoveChecker().getMovables()

                            gameState.copy(pieces = list, canMove = movables, turn = it.turn)
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
        if(provider == null) return
        val mover = if(provider.getStateStreamer().getState().value.turn == Turn.WHITE)
            provider.getWhiteMover() else provider.getBlackMover()
        mover.move(point1, point2)
    }
}

data class PlayerState(
    val name: String = "",
    val turn: Boolean = true
)