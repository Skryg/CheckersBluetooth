package com.skryg.checkersbluetooth.game.ui.view

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.services.BluetoothGameProvider
import com.skryg.checkersbluetooth.game.services.GameController
import com.skryg.checkersbluetooth.game.services.GameProvider
import com.skryg.checkersbluetooth.game.ui.utils.BoardUpdater
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.UiState
import com.skryg.checkersbluetooth.sound.GameSounds
import com.skryg.checkersbluetooth.sound.Sound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.ArrayList

class BluetoothGameViewModel(private val provider: BluetoothGameProvider, gameSounds: GameSounds? = null)
    : ViewModel(), BoardUpdater {
    private val _gameUiState = MutableStateFlow(UiState())
    val gameUiState = _gameUiState.asStateFlow()
    private val showDrawDialog = mutableStateOf(false)
    val localTurn = provider.localPlayerTurn
    private var cachedPoint: Point? = null

    val myName = if(localTurn == Turn.WHITE)
        provider.getStateStreamer().getStateFlow().value.nameWhite
    else
        provider.getStateStreamer().getStateFlow().value.nameBlack
    val opponentName = if(localTurn == Turn.WHITE)
        provider.getStateStreamer().getStateFlow().value.nameBlack
    else
        provider.getStateStreamer().getStateFlow().value.nameWhite

    init {
        gameSounds?.load(Sound.MOVE)
        gameSounds?.load(Sound.WIN)
        gameSounds?.load(Sound.LOSE)
        println("BluetoothGameViewModel initialized with provider: $provider")
        viewModelScope.launch {
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

                    when (it.result) {
                        GameResult.WHITE_WON -> {
                            val sound = if(localTurn == Turn.WHITE) Sound.WIN else Sound.LOSE
                            gameSounds?.play(sound)
                        }
                        GameResult.BLACK_WON -> {
                            val sound = if(localTurn == Turn.BLACK) Sound.WIN else Sound.LOSE
                            gameSounds?.play(sound)
                        }
                        GameResult.DRAW -> {
                            gameSounds?.play(Sound.LOSE)
                        }
                        GameResult.ONGOING -> {
                            gameSounds?.play(Sound.MOVE)
                        }
                    }

                    return@update gameState.copy(
                        pieces = list,
                        canMove = movables,
                        turn = it.turn,
                        result = it.result
                    )
                }
                if(cachedPoint != null) {
                    updateSelected(cachedPoint)
                }
            }

        }

    }

    override fun updateSelected(point: Point?) {
        cachedPoint = point
        val list = point?.let{ provider.getMoveChecker().getMoves(point) }
        _gameUiState.update{
            it.copy(movePoints = list ?: emptyList())
        }
    }

    override fun move(point1: Point, point2: Point) {
        viewModelScope.launch {
            val mover = provider.getLocalPlayerMover()
            mover.move(point1, point2)
        }
    }

    fun resign() {
        viewModelScope.launch {
            val mover = provider.getLocalPlayerMover()
            mover.resign()
        }
    }
}