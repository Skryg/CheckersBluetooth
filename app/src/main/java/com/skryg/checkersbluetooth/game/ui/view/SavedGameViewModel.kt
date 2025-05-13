package com.skryg.checkersbluetooth.game.ui.view

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.database.Move
import com.skryg.checkersbluetooth.game.logic.core.PieceInitializer
import com.skryg.checkersbluetooth.game.logic.core.SpectateGameFactory
import com.skryg.checkersbluetooth.game.logic.core.standard.StandardPieceInitializer
import com.skryg.checkersbluetooth.game.logic.core.standard.spectate.StandardSpectateFactory
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.logic.model.toPoint
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.sound.GameSounds
import com.skryg.checkersbluetooth.sound.Sound
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.logging.Logger


class SavedGameViewModel(repository: GameRepository,
                         gid: Long,
                         spectateFactory: SpectateGameFactory = StandardSpectateFactory(),
                         private val gameSounds: GameSounds? = null)
    : ViewModel() {
    private lateinit var movesList: List<Move>
    private var movePointer = 0

    private val initializer = spectateFactory.getBoardInitializer()
    private val mover = spectateFactory.getSpectateMover()

    private val mutablePiecesFlow = MutableStateFlow(
        MovesState(pieces = emptyList()))

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
            initializer.initialize()

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

            spectateFactory
                .getSpectateStateStreamer()
                .getStateFlow()
                .collect { gameState ->
                    Logger.getLogger("SavedGameViewModel")
                        .info("Game state updated: $gameState")
                    mutablePiecesFlow.update {
                        gameState.board.getAllPieces().map { piece ->
                            PieceUi(
                                point = piece.first,
                                isDark = (piece.second.color == Turn.BLACK),
                                isKing = piece.second.king,
                            )
                        }.let { pieces ->
                            it.copy(
                                pieces = pieces,
                                movePointer = movePointer,
                            )
                        }
                    }
            }


        }
    }

    fun moveNext() = runBlocking {
        if(movePointer < movesList.size) {
            val move = movesList[movePointer]
            val from = move.from.toPoint()
            val to = move.to.toPoint()

            movePointer++
            mover.move(from, to)
            gameSounds?.play(Sound.MOVE)
        }
    }

    fun movePrev() = runBlocking {
        if(movePointer > 0) {
            movePointer--
            mover.undo()
            gameSounds?.play(Sound.MOVE)
        }
    }
}
