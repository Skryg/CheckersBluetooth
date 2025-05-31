package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.game.logic.core.MoveChecker
import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn

class StandardMoveChecker(private val gameState: GameState) : MoveChecker {
    override fun getMovables(): List<Point> {
        val attackers = getAttackers(gameState.lastMove)
        if (attackers.isNotEmpty() || gameState.lastMove != null)
            return attackers

        return getNoAttackMovables()
    }

    override fun getMoves(point: Point): List<Point> {
        if(!getMovables().contains(point)){
            return emptyList()
        }

        val attacks = getAttacks(point, gameState.lastMove)
        if(attacks.isNotEmpty() || gameState.lastMove != null)
            return attacks


        return getNotAttackMoves(point)
    }

    private fun getAttackers(charge: Point? = null): List<Point>{
        val list = ArrayList<Point>()
        gameState.board.forEach { point, piece ->
            if(piece?.color == gameState.turn && getAttacks(point, charge).isNotEmpty()) {
                list.add(point)
            }
        }
        return list
    }

    //get attacks for one point
    private fun getAttacks(point: Point, charge: Point?): List<Point>{
        if (charge != null && charge != point) return emptyList()
        val piece = gameState.board.getPiece(point)
        if (piece == null || piece.color != gameState.turn) return emptyList()

        val allSites: (Int) -> List<Point> = { depth ->
            val list = ArrayList<Point>()
            for (i in intArrayOf(-1,1)){
                for (j in intArrayOf(-1,1)){
                    val offset = Point(i, j)
                    list.addAll(checkRecursive(point+offset, offset, piece.color, false, depth))
                }
            }
            list
        }

        val twoSitesPawn: () -> List<Point> = {
            val list = ArrayList<Point>()
            val j = if(piece.color == Turn.BLACK) 1 else -1
            for (i in intArrayOf(-1,1)){
                val offset = Point(i, j)
                list.addAll(checkRecursive(point+offset, offset, piece.color, false, 2))
            }
            list
        }

        if(piece.king) {
            return allSites(10)
        }
        if(charge != null) return allSites(2)
        return twoSitesPawn()
    }

    private fun checkRecursive(
        point: Point,
        offset: Point,
        color: Turn,
        jumpOver: Boolean,
        depth: Int = 2
    ): List<Point>{
        if(depth==0) return emptyList()
        val (x,y) = point
        val (dx,dy) = offset
        if(x !in 0..7 || y !in 0..7) return emptyList()
        val piece = gameState.board.getPiece(Point(x,y))
        if(piece == null){
            val list = checkRecursive(
                Point(
                    x+dx,
                    y+dy
                ), offset, color, jumpOver, depth-1)
            if(jumpOver) return list + point
            return list
        }
        if(piece.color == color) return emptyList()
        if(!jumpOver){
            return checkRecursive(Point(x+dx, y+dy), offset, color, true, depth-1)
        }
        return emptyList()
    }

    private fun getNotAttackMoves(point: Point): List<Point> {
        val piece = gameState.board.getPiece(point) ?: return emptyList()
        if(piece.color != gameState.turn) return emptyList()

        val list = ArrayList<Point>()

        if(piece.king){
            for(i in intArrayOf(-1,1)){
                for(j in intArrayOf(-1,1)){
                    val offset = Point(i, j)
                    list.addAll(checkRecursive(point+offset, offset, piece.color, true, 10))
                }
            }
            return list
        }
        val offset = if(piece.color == Turn.BLACK) 1 else -1


        list.addAll(checkRecursive(point+ Point(
            1,
            offset
        ), Point(1, offset), piece.color, true, 1))
        list.addAll(checkRecursive(point+ Point(
            -1,
            offset
        ), Point(-1, offset), piece.color, true, 1))
        return list
    }

    private fun getNoAttackMovables(): List<Point>{
        val list = ArrayList<Point>()
        gameState.board.forEach { point, piece ->
            if(piece?.color == gameState.turn && getNotAttackMoves(point).isNotEmpty())
                list.add(point)
        }
        return list
    }
}