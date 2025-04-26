package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.GameInitializer
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.Piece
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.logic.model.toPoint
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi

class StandardGameInitializer(private val state: GameState,
                              private val moveChecker: MoveChecker,
                              private val repository: GameRepository? = null
) : GameInitializer {
    private val moverWrapper = InitializerMoverWrapper(moveChecker, state)

    override suspend fun initialize() {
        val piecesList = ArrayList<PieceUi>()
        for (y in 0 until 2) {
            for (x in 0 until 8) {
                if ((x + y) % 2 != 0){
                    state.board.setPiece(Point(x,y), Piece(Turn.BLACK))
                }
            }
        }
        for (y in 6 until 8) {
            for (x in 0 until 8) {
                if ((x + y) % 2 != 0){
                    state.board.setPiece(Point(x,y), Piece(Turn.WHITE))
                }
            }
        }

    }

    override suspend fun load(gid: Long) {
        if(repository == null)
            throw NullPointerException()
        val entity = repository.getGamesWithMoves(gid)
        if (entity != null) {
            state.result = GameResult.entries[entity.game.winner]
            for (move in entity.moves) {
                moverWrapper.move(move.from.toPoint(), move.to.toPoint())
            }
        }
    }

}

private class InitializerMoverWrapper(private val checker: MoveChecker,
                                      private val state: GameState) {
    private val stateStreamer = StandardStateStreamer(state)
    private val whiteMover = StandardMover(Turn.WHITE, checker, state, stateStreamer)
    private val blackMover = StandardMover(Turn.BLACK, checker, state, stateStreamer)

    suspend fun move(p1: Point, p2: Point): Boolean {
        if(state.turn == Turn.WHITE) {
            return whiteMover.move(p1, p2)
        }
        return blackMover.move(p1, p2)
    }
}