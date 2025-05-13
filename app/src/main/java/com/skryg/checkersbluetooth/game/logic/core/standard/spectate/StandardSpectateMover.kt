package com.skryg.checkersbluetooth.game.logic.core.standard.spectate

import com.skryg.checkersbluetooth.game.logic.core.BaseStateStreamer
import com.skryg.checkersbluetooth.game.logic.core.MoveStage
import com.skryg.checkersbluetooth.game.logic.core.ReversibleMover
import com.skryg.checkersbluetooth.game.logic.core.standard.MoveData
import com.skryg.checkersbluetooth.game.logic.core.standard.PerformMove
import com.skryg.checkersbluetooth.game.logic.core.standard.TerminalStage
import com.skryg.checkersbluetooth.game.logic.core.standard.TryPromote
import com.skryg.checkersbluetooth.game.logic.model.MutableGameBoard
import com.skryg.checkersbluetooth.game.logic.model.Point

class StandardSpectateMover(private val board: MutableGameBoard,
                            private val stateStreamer: BaseStateStreamer? = null)
    : ReversibleMover {
    private val moveList = mutableListOf<MoveData>()
    private val promoteList = mutableListOf<Boolean>()
    private val handler: MoveStage

    init {
        val performMove = PerformMove(board, moveList)
        val tryPromote = TryPromote(board, promoteList)
        val terminalStage = TerminalStage()

        performMove.setNext(tryPromote, tryPromote)
        tryPromote.setNext(terminalStage)
        handler = performMove
    }

    override suspend fun undo() {
        val lastData = moveList.removeLastOrNull() ?: return
        val promote = promoteList.removeLastOrNull() ?: return
        val (from, to) = lastData
        val piece = board.getPiece(to) ?: return

        if (promote) {
            board.setPiece(from, piece.copy(king = false))
        } else {
            board.setPiece(from, piece)
        }
        board.setPiece(to, null)
        lastData.killed.forEach { (point, piece) ->
            board.setPiece(point, piece)
        }

        stateStreamer?.update()
    }

    override suspend fun move(p1: Point, p2: Point): Boolean {
        val res = handler.handle(p1, p2)
        stateStreamer?.update()
        return res
    }
}