package com.skryg.checkersbluetooth.game.ui.local

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.game.GameController
import com.skryg.checkersbluetooth.game.ui.utils.BoardUpdater
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.Point
import com.skryg.checkersbluetooth.game.ui.utils.UiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

            }
        }

    }


    override fun updateSelected(point: Point?) {
        val list = point?.let{gameController.calculateMoves(point)}
        _gameUiState.update{
            it.copy(canMove = list ?: emptyList())
        }
    }

    override fun PieceUi.moveTo(point: Point) {
        gameController.makeMove(this.point, point)
    }
}

data class PlayerState(
    val name: String = "",
    val turn: Boolean = true
)