package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.MoveStage
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.MutableGameBoard
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import java.util.logging.Logger
import kotlin.math.abs

private suspend fun MoveStage.handle(idx: Int, p1:Point, p2:Point): Boolean{
    if (list.size > idx) {
        return list[idx].handle(p1, p2)
    }
    Logger.getLogger("MoveStage").info("No next move stage of id $idx")
    return false
}

// If can move than first handler is called
class CheckMove(private val moveChecker: MoveChecker,
                private val checkSecond: Boolean = false) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        val p = if (checkSecond) p2 else p1
        val idx = if (moveChecker.getMoves(p).isNotEmpty()) 0 else 1
        Logger.getLogger("CheckMove").info("Check move $p1 to $p2. Can move to: ${moveChecker.getMoves(p)}")
        return handle(idx, p1, p2)
    }
}

class TryPromote(private val board: MutableGameBoard) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        fun isLastPoint(point: Point, color: Turn): Boolean{
            return if(color == Turn.WHITE) point.y == 0 else point.y == 7
        }

        fun tryPromote(p1: Point) {
            val p = board.getPiece(p1) ?: return
            if (isLastPoint(p1, p.color)) {
                board.setPiece(p1, p.copy(king = true))
            }
        }
        tryPromote(p2)
        return handle(0,p1,p2)
    }
}


// If moved and it was not an attack then 0, else 1
class PerformMove(val board: MutableGameBoard) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {

        board.getPiece(p1)?.let {
            var wasAttack = false
            board.setPiece(p2, it)
            board.setPiece(p1, null)
            
            val p = (p2 - p1)/ abs((p2 - p1).x)
            var px = p1.copy() + p
            while(px != p2 && (px.x in 0..7) && (px.y in 0..7)){

                if (board.getPiece(px) != null) {
                    wasAttack = true
                }
                board.setPiece(px, null)
                px += p
            }

            val idx = if(wasAttack) 1 else 0
            Logger.getLogger("Move").info("Move $p1 to $p2. Was attack: $wasAttack")
            return handle(idx, p1, p2)
        }
        return false
    }

}


class SwitchTurn(private val state: GameState,
                 private val moveChecker: MoveChecker?=null,
                 private val stateStreamer: StateStreamer?=null) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        state.turn = if (state.turn == Turn.BLACK) Turn.WHITE else Turn.BLACK

        if(stateStreamer != null && moveChecker != null
            && moveChecker.getMovables().isEmpty()) {
            if (state.turn == Turn.BLACK) {
                stateStreamer.setWin(Turn.WHITE)
            } else {
                stateStreamer.setWin(Turn.BLACK)
            }
            Logger.getLogger("SwitchTurn").info("Game over")
        } else {
            Logger.getLogger("SwitchTurn").info("Next turn: ${state.turn}")
        }

        Logger.getLogger("SwitchTurn").info("Switch turn to ${state.turn}")
        return handle(0,p1,p2)
    }
}

class RepositorySave(private val repository: GameRepository, private val state: GameState) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        repository.insert(com.skryg.checkersbluetooth.database.Move(gameId = state.gameId, from = p1.toString(), to = p2.toString()))
        return handle(0,p1,p2)
    }
}

class SetLast(private val state: GameState) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        state.lastMove = p2
        return handle(0,p1,p2)
    }
}

class ResetLast(private val state: GameState) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        state.lastMove = null
        return handle(0,p1,p2)
    }
}

class ResetDraw(private val state: GameState) : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        state.resetDraw()
        return handle(0,p1,p2)
    }
}

class CheckTurn(private val state: GameState,
                private val color: Turn)
    : MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        if(state.turn == color) {
            return handle(0,p1,p2)
        } else {
            Logger.getLogger("CheckTurn").info("Wrong turn")
        }
        return false
    }
}

class TerminalStage: MoveStage() {
    override suspend fun handle(p1: Point, p2: Point): Boolean {
        Logger.getLogger("TerminalStage").info("Terminal stage")
        return true
    }

}