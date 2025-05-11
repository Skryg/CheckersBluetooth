package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.game.logic.core.GameInitializer
import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.core.MoveStage
import com.skryg.checkersbluetooth.game.logic.core.Mover
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.Piece
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.logic.model.toPoint
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi

class StandardGameInitializer(private val state: GameState,
                              moveChecker: MoveChecker,
                              private val stateStreamer: StandardStateStreamer,
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

        stateStreamer.update()

    }

    override suspend fun load(gid: Long) {
        state.gameId = gid
        if(repository == null)
            throw NullPointerException()
        val entity = repository.getGameWithMoves(gid)
        if (entity != null) {
            state.result = entity.game.winner
            for (move in entity.moves) {
                moverWrapper.move(move.from.toPoint(), move.to.toPoint())
            }
        }
        stateStreamer.update()
    }

}

private class InitializerMoverWrapper(checker: MoveChecker,
                                      state: GameState): Mover {
    val handler: MoveStage

    init {
        val performMove = PerformMove(state.board)
        val switchTurn = SwitchTurn(state)
        val terminalStage = TerminalStage()
        val tryPromote = TryPromote(state.board)
        val checkMoveAttack = CheckMove(checker, true)
        val setLast = SetLast(state)
        val resetLast = ResetLast(state)
        val resetLast2 = ResetLast(state)

        resetLast.setNext(listOf(performMove))
        performMove.setNext(listOf(switchTurn, setLast))
        setLast.setNext(listOf(checkMoveAttack))
        checkMoveAttack.setNext(listOf(tryPromote, resetLast2))
        resetLast2.setNext(listOf(switchTurn))
        switchTurn.setNext(listOf(tryPromote))
        tryPromote.setNext(listOf(terminalStage))
        handler = resetLast
    }


    override suspend fun move(p1: Point, p2: Point): Boolean {
        return handler.handle(p1, p2)
    }
}