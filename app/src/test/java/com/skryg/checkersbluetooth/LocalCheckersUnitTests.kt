package com.skryg.checkersbluetooth

import com.skryg.checkersbluetooth.game.logic.GameControllerImpl
import com.skryg.checkersbluetooth.game.logic.GameProvider
import com.skryg.checkersbluetooth.game.logic.LocalGameProvider
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.Point
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test


class LocalCheckersUnitTests {
    private val gameController = GameControllerImpl()
    init{
        gameController.createGame(LocalGameProvider())
    }

    @Test
    fun boardTest(){
        val state = gameController.getGameState()
        val pieces = state.value.pieces
        assertEquals(pieces.size, 16)

        val blackPoints = listOf(Point(1,0), Point(3,0), Point(5,0), Point(7,0),
            Point(0,1), Point(2,1), Point(4,1), Point(6,1)
        )
        val whitePoints = listOf(Point(1,6), Point(3,6), Point(5,6), Point(7,6),
            Point(0,7), Point(2,7), Point(4,7), Point(6,7)
        )

        assertTrue(pieces.containsAll(
            blackPoints.map{
                point -> PieceUi(isDark= true, point = point)
            }+whitePoints.map{
                point -> PieceUi(isDark = false, point = point)
            }
        ))

    }

    private fun makeMove(point1: Point, point2: Point, dark: Boolean){
        gameController.makeMove(point1, point2)

        val pieces = gameController.getGameState().value.pieces
        assertTrue(
            pieces.contains(PieceUi(isDark = dark, point = point2))
                    && !pieces.contains(PieceUi(isDark = dark, point = point1))
        )
    }

    @Test
    fun moveTest(){
        makeMove(Point(1,6), Point(0,5), false)
    }

    @Test
    fun moveTwoTest(){
        makeMove(Point(1,6), Point(0,5), false)
        makeMove(Point(0,1), Point(1,2), true)
    }

    @Test
    fun doubleMoveTest(){
        assertThrows(
            GameProvider.InvalidMoveException::class.java
        ) {
            makeMove(Point(1, 6), Point(0, 5), false)
            makeMove(Point(0, 5), Point(1, 4), false)
        }
    }

    // black on (2,3), (4,3). white on (1,4) white turn
    private fun attackSituation(){
        makeMove(Point(1,6), Point(0,5), false)
        makeMove(Point(0,1), Point(1, 2), true)
        makeMove(Point(3,6), Point(2, 5), false)
        makeMove(Point(1, 2), Point(2,3), true)
        makeMove(Point(7,6), Point(6,5), false)
        makeMove(Point(6, 1), Point(5,2), true)
        makeMove(Point(2,5), Point(1,4), false)
        makeMove(Point(5,2), Point(4,3), true)
    }

    @Test
    fun movablesTest(){
        attackSituation()
        assertEquals(listOf(Point(1,4)), gameController.movablePieces())
    }

    @Test
    fun movablesTwoTest(){
        assertEquals(listOf(Point(1,6), Point(3,6), Point(5,6), Point(7,6)),
            gameController.movablePieces())
    }

    @Test
    fun movablesBlackTest(){
        makeMove(Point(1,6), Point(0,5),false)
        assertEquals(listOf(Point(0,1), Point(2,1,), Point(4,1),Point(6,1)),
            gameController.movablePieces())
    }

    @Test
    fun doubleAttackTest(){
        attackSituation()
        assertEquals(listOf(Point(3,2)), gameController.calculateMoves(Point(1,4)))
        makeMove(Point(1,4), Point(3,2), false)
        assertEquals(listOf(Point(5,4)), gameController.calculateMoves(Point(3,2)))
        assertEquals(listOf(Point(3,2)), gameController.movablePieces())
        makeMove(Point(3,2), Point (5,4), false)
    }


}