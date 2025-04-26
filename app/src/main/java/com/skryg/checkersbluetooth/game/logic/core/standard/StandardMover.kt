package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.database.Move
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.Mover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn


class StandardMover(val color: Turn,
                    private val checker: MoveChecker,
                    private val state: GameState,
                    private val stateStreamer: StateStreamer? = null,
                    private val repository: GameRepository? = null
) : Mover {
    private fun isLastPoint(point: Point): Boolean{
        val turn = color
        return if(turn == Turn.WHITE) point.y == 0 else point.y == 7
    }

    private fun tryPromote(p1: Point) {
        val p = state.board.getPiece(p1) ?: return
        if (isLastPoint(p1)) {
            state.board.setPiece(p1, p.copy(king = true))
        }
    }

    override suspend fun move(p1: Point, p2: Point): Boolean {
        state.resetDraw()

        val piece = state.board.getPiece(p1)
        if (piece == null || piece.color != color || p2 !in checker.getMoves(p1)) {
            return false
        }

        state.board.setPiece(p2, piece)

        var px = p1.copy()
        val p = (p2 - p1)/(p2 - p1).x
        while(px != p2 && p.x in (0..7) && p.y in (0..7)){
            state.board.setPiece(px)
            px += p
        }

        repository?.insert(Move(gameId = state.gameId, from=p1.toString(), to=p2.toString()))
        tryPromote(p2)

        state.lastMove = p2
        val moves = checker.getMoves(p2)
        if(moves.isEmpty()) {
            state.lastMove = null
            state.turn = if(color == Turn.BLACK) Turn.WHITE else Turn.BLACK
        }
        stateStreamer?.update()
        return true
    }

    override suspend fun resign() {
        if (color == Turn.BLACK) {
            stateStreamer?.setWin(Turn.WHITE)
        } else {
            stateStreamer?.setWin(Turn.BLACK)
        }
    }

    override suspend fun draw() {
        state.draw(color)
        if(state.canDraw()){
            stateStreamer?.setDraw()
        }
    }
}