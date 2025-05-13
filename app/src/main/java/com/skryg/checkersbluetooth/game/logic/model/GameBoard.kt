package com.skryg.checkersbluetooth.game.logic.model

open class GameBoard {
    protected val size = 8
    protected val board: Array<Piece?> = Array(size*size){null}

    fun getPiece(point: Point): Piece? {
        return board[point.x*size+point.y];
    }

    fun forEach( fn: (point: Point, piece: Piece? ) -> Unit) {
        board.forEachIndexed { idx, piece ->

            fn(Point(idx/8,idx%8), piece)

        }
    }

    fun getAllPieces(): List<Pair<Point, Piece>> {
        val list = ArrayList<Pair<Point, Piece>>()
        for (i in 0 until size) {
            for (j in 0 until size) {
                val piece = getPiece(Point(i,j))
                if(piece != null) {
                    list.add(Pair(Point(i,j), piece))
                }
            }
        }
        return list
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameBoard

        return board.contentEquals(other.board)
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + board.contentHashCode()
        return result
    }
}

class MutableGameBoard: GameBoard() {
    fun setPiece(point: Point, piece: Piece? = null) {
        board[point.x*size + point.y] = piece
    }
}

data class Piece(val color: Turn, val king: Boolean = false)
data class Point(val x: Int, val y: Int) {
    operator fun minus(p1: Point) : Point {
        return Point(x - p1.x, y - p1.y)
    }

    operator fun plus(p1: Point): Point {
        return Point(x + p1.x, y + p1.y)
    }

    operator fun div(int: Int) : Point {
        return Point(x/int, y/int)
    }

    override fun toString(): String{
        return x.toString() + y.toString()
    }
}

fun String.toPoint(): Point {
    return Point(this[0] - '0', this[1] - '0')
}

