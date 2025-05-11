package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.database.Move
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.Mover
import com.skryg.checkersbluetooth.game.logic.core.PlayerMover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlin.math.abs


class StandardMover(val color: Turn,
                    private val checker: MoveChecker,
                    private val state: GameState,
                    private val stateStreamer: StateStreamer? = null,
                    private val repository: GameRepository? = null
) : PlayerMover {
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

    override suspend fun resign() {
        TODO("Not yet implemented")
    }

    override suspend fun draw() {
        TODO("Not yet implemented")
    }

    override suspend fun move(p1: Point, p2: Point): Boolean {
        state.resetDraw()

        val piece = state.board.getPiece(p1)
        if (piece == null || piece.color != color || p2 !in checker.getMoves(p1)) {
            return false
        }

        state.board.setPiece(p2, piece)
        state.lastMove = null


        var px = p1.copy()
        val p = (p2 - p1)/abs((p2 - p1).x)
        while(px != p2 && (px.x in 0..7) && (px.y in 0..7)){
            state.board.getPiece(px)?.let{
                if(it.color != color) {
                    state.lastMove = p2
                }
                state.board.setPiece(px, null)
            }
            px += p
        }

        repository?.insert(Move(gameId = state.gameId, from=p1.toString(), to=p2.toString()))
        tryPromote(p2)

        if(state.lastMove != null) {
            val moves = checker.getMoves(p2)
            if(moves.isEmpty()) {
                state.lastMove = null
                state.turn = if(color == Turn.BLACK) Turn.WHITE else Turn.BLACK
            }
        }

        stateStreamer?.update()
        return true
    }


}