package com.skryg.checkersbluetooth.game.ui.view

import android.media.AudioAttributes
import android.media.SoundPool
import android.provider.MediaStore.Audio
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.services.GameController
import com.skryg.checkersbluetooth.game.services.LocalGameProvider
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

class LocalGameViewModel(private val provider: LocalGameProvider, gameSounds: GameSounds? = null)
    : ViewModel(), BoardUpdater {
    private val _gameUiState = MutableStateFlow(UiState())
    val gameUiState = _gameUiState.asStateFlow()

    init {
        gameSounds?.load(Sound.MOVE)
        gameSounds?.load(Sound.WIN)
        gameSounds?.load(Sound.LOSE)

        println("Provider: $provider, gameId: ${provider.id}")
        println("New GameViewModel")
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

                    when (it.result) {
                        GameResult.WHITE_WON, GameResult.BLACK_WON -> {
                            gameSounds?.play(Sound.WIN)
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


            }
        }
    }

    override fun updateSelected(point: Point?) {
        val list = point?.let{ provider.getMoveChecker().getMoves(point) }
        _gameUiState.update{
            it.copy(movePoints = list ?: emptyList())
        }
    }

    override fun move(point1: Point, point2: Point) {
        viewModelScope.launch {
            val mover = if(provider.getStateStreamer().getStateFlow().value.turn == Turn.WHITE)
                provider.getWhiteMover() else provider.getBlackMover()
            mover.move(point1, point2)
        }
    }

    fun resign(color: Turn) {
        viewModelScope.launch {
            val mover = if(color == Turn.WHITE)
                provider.getWhiteMover() else provider.getBlackMover()
            mover.resign()
        }
    }

    fun proposeDraw(color: Turn) {
        viewModelScope.launch {
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