package com.skryg.checkersbluetooth.game.ui.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.game.logic.GameController
import com.skryg.checkersbluetooth.game.logic.LocalGameProvider
import com.skryg.checkersbluetooth.game.ui.utils.BoardUpdater
import com.skryg.checkersbluetooth.game.ui.utils.Point
import com.skryg.checkersbluetooth.game.ui.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LocalGameViewModel(private val gameController: GameController) : ViewModel(), BoardUpdater {
    private val _gameUiState = MutableStateFlow(UiState())
    val gameUiState = _gameUiState.asStateFlow()

    private val _playerWhiteState = MutableStateFlow(PlayerState(name = "Player White"))
    private val _playerBlackState = MutableStateFlow(PlayerState(name = "Player Black", turn = false))
    val playerWhiteState = _playerWhiteState.asStateFlow()
    val playerBlackState = _playerBlackState.asStateFlow()


    init {
        gameController.createGame(LocalGameProvider())
        viewModelScope.launch {
            gameController.getGameState().collect {
                _gameUiState.update { gameState ->
                    if(it.pieces != gameState.pieces)
                        gameState.copy(pieces = it.pieces)
                    else gameState
                }
                if(_playerWhiteState.value.turn == it.turn){
                    _playerWhiteState.update { whiteState->
                        whiteState.copy(turn = !it.turn, name = it.white)
                    }
                    _playerBlackState.update{ blackState->
                        blackState.copy(turn = it.turn, name = it.black)
                    }
                }
                val movables = gameController.movablePieces()
                _gameUiState.update { gameState ->
                    gameState.copy(canMove = movables)
                }

            }
        }

    }


    override fun updateSelected(point: Point?) {
        val list = point?.let{gameController.calculateMoves(point)}
        _gameUiState.update{
            it.copy(movePoints = list ?: emptyList())
        }
    }

    override fun move(point1: Point, point2: Point) {
        gameController.makeMove(point1, point2)
    }
}

data class PlayerState(
    val name: String = "",
    val turn: Boolean = true
)