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
    private val pieceInitializer = StandardPieceInitializer()

    override suspend fun initialize() {
        pieceInitializer.initialize().forEach {
            state.board.setPiece(
                it.point,
                Piece(if (it.isDark) Turn.BLACK else Turn.WHITE)
            )
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
            state.nameWhite = entity.game.whitePlayer
            state.nameBlack = entity.game.blackPlayer

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

        resetLast.setNext(performMove)
        performMove.setNext(switchTurn, setLast)
        setLast.setNext(checkMoveAttack)
        checkMoveAttack.setNext(tryPromote, resetLast2)
        resetLast2.setNext(switchTurn)
        switchTurn.setNext(tryPromote)
        tryPromote.setNext(terminalStage)
        handler = resetLast
    }


    override suspend fun move(p1: Point, p2: Point): Boolean {
        return handler.handle(p1, p2)
    }
}