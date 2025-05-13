package com.skryg.checkersbluetooth.game.logic.core.standard.spectate

import com.skryg.checkersbluetooth.game.logic.core.BaseStateStreamer
import com.skryg.checkersbluetooth.game.logic.core.GameInitializer
import com.skryg.checkersbluetooth.game.logic.core.Initializer
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.ReversibleMover
import com.skryg.checkersbluetooth.game.logic.core.SpectateGameFactory
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.core.standard.StandardGameInitializer
import com.skryg.checkersbluetooth.game.logic.core.standard.StandardPieceInitializer
import com.skryg.checkersbluetooth.game.logic.model.GameBoard
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.GameStateReadonly
import com.skryg.checkersbluetooth.game.logic.model.MutableGameBoard
import com.skryg.checkersbluetooth.game.logic.model.Piece
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StandardSpectateFactory: SpectateGameFactory {
    private val board by lazy { MutableGameBoard() }
    private val pieceInitializer = StandardPieceInitializer()
    private val stateStreamer = object : BaseStateStreamer {
        private val mutableStateFlow = MutableStateFlow(GameState(board = board).copy())

        override fun getStateFlow(): StateFlow<GameStateReadonly> {
            return mutableStateFlow.asStateFlow()
        }
        private var updates: Long = 0
        override fun update() {

            mutableStateFlow.update{
                it.copy(
                    turn = Turn.BLACK,
                    result = GameResult.ONGOING,
                    lastMove = null,
                    board = board,
                    gameId = updates++,
                    nameWhite = "",
                    nameBlack = ""
                )
            }
        }
    }

    override fun getSpectateMover(): ReversibleMover {
        return StandardSpectateMover(board, stateStreamer)
    }

    override fun getSpectateStateStreamer(): BaseStateStreamer {
        return stateStreamer
    }

    override fun getBoardInitializer(): Initializer {
        return object : Initializer {
            override suspend fun initialize() {
                for (pieceUi in pieceInitializer.initialize()) {
                    board.setPiece(pieceUi.point,
                        Piece(
                            color = if (pieceUi.isDark) Turn.BLACK else Turn.WHITE,
                            king = pieceUi.isKing
                        ))
                }
                stateStreamer.update()
            }
        }
    }

}