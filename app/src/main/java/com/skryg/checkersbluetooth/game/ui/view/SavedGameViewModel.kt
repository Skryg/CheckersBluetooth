package com.skryg.checkersbluetooth.game.ui.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.database.Move
import com.skryg.checkersbluetooth.game.logic.core.PieceInitializer
import com.skryg.checkersbluetooth.game.logic.core.standard.StandardPieceInitializer
import com.skryg.checkersbluetooth.game.logic.model.toPoint
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.sound.GameSounds
import com.skryg.checkersbluetooth.sound.Sound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SavedGameViewModel(repository: GameRepository,
                         gid: Long,
                         pieceInitializer: PieceInitializer = StandardPieceInitializer(),
                         private val gameSounds: GameSounds? = null)
    : ViewModel() {
    private lateinit var movesList: List<Move>
    private var movePointer = 0

    private val mutablePiecesFlow = MutableStateFlow(
        MovesState(pieces = pieceInitializer.initialize()))

    val movesFlow = mutablePiecesFlow.asStateFlow()
    var game by mutableStateOf<GameEntity?>(null)
        private set

    data class MovesState(
        val movePointer: Int = 0,
        val maxMoves: Int = 0,
        val pieces: List<PieceUi> = emptyList(),
    )

    init {
        gameSounds?.load(Sound.MOVE)
        viewModelScope.launch {
            val gameWithMoves = repository.getGameWithMoves(gid)

            gameWithMoves?.let{
                movesList = it.moves
                game = it.game
            }

            mutablePiecesFlow.update {
                it.copy(
                    maxMoves = movesList.size,
                    movePointer = movePointer
                )
            }
        }
    }

    fun moveNext() {
        if(movePointer < movesList.size) {
            val move = movesList[movePointer]
            val from = move.from.toPoint()
            val to = move.to.toPoint()

            movePointer++
            mutablePiecesFlow.update {
                it.copy(pieces = it.pieces.map { piece ->
                    if(piece.point == from) {
                        piece.copy(point = to)
                    } else {
                        piece
                    }
                }, movePointer = movePointer)
            }
            gameSounds?.play(Sound.MOVE)
        }
    }

    fun movePrev() {
        if(movePointer > 0) {
            movePointer--
            val move = movesList[movePointer]
            val from = move.from.toPoint()
            val to = move.to.toPoint()

            mutablePiecesFlow.update {
                it.copy(pieces = it.pieces.map { piece ->
                    if(piece.point == to) {
                        piece.copy(point = from)
                    } else {
                        piece
                    }
                }, movePointer = movePointer)
            }
            gameSounds?.play(Sound.MOVE)
        }
    }
}
