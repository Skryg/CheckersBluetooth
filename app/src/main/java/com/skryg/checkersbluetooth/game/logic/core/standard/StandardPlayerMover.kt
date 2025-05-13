package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.MoveStage
import com.skryg.checkersbluetooth.game.logic.core.PlayerMover
import com.skryg.checkersbluetooth.game.logic.core.StateStreamer
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn

class StandardPlayerMover(private val turn: Turn,
                          moveChecker: MoveChecker,
                          private val gameState: GameState,
                          private val stateStreamer: StateStreamer? = null,
                          repository: GameRepository? = null)
    : PlayerMover {

    private val moveStage: MoveStage
    init {

        val checkTurn = CheckTurn(gameState, turn)
        val resetDraw = ResetDraw(gameState)
        val resetLast = ResetLast(gameState)
        val setLast = SetLast(gameState)

        val checkMove = CheckMove(moveChecker)
        val checkMoveAttack = CheckMove(moveChecker, true)
        val tryPromote = TryPromote(gameState.board)
        val performMove = PerformMove(gameState.board)
        val switchTurn = SwitchTurn(gameState, moveChecker, stateStreamer)
        val repositorySave = repository?.let { RepositorySave(it, gameState) }
        val terminalStage = TerminalStage()
        val resetLast2 = ResetLast(gameState)

        checkTurn.setNext(resetDraw)
        resetDraw.setNext(checkMove)
        checkMove.setNext(resetLast)
        resetLast.setNext(performMove)
        performMove.setNext(switchTurn, setLast)
        setLast.setNext(checkMoveAttack)
        checkMoveAttack.setNext(tryPromote, resetLast2)
        resetLast2.setNext(switchTurn)
        switchTurn.setNext(tryPromote)

        if(repositorySave != null) {
            tryPromote.setNext(repositorySave)
            repositorySave.setNext(terminalStage)
        } else {
            tryPromote.setNext(terminalStage)
        }

        moveStage = checkMove
    }

    override suspend fun resign() {
        if (turn == Turn.BLACK) {
            stateStreamer?.setWin(Turn.WHITE)
        } else {
            stateStreamer?.setWin(Turn.BLACK)
        }
    }

    override suspend fun draw() {
        gameState.draw(turn)
        if(gameState.canDraw()){
            stateStreamer?.setDraw()
        }
    }

    override suspend fun move(p1: Point, p2: Point): Boolean {
        val result =  moveStage.handle(p1, p2)
        stateStreamer?.update()
        return result
    }

}
