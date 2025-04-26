package com.skryg.checkersbluetooth.game.logic.model

class GameBoard {
    private val board: Array<Array<Piece?>> = Array(8){Array(8){null}}

    fun getPiece(point: Point): Piece? {
        return board[point.x][point.y];
    }

    fun setPiece(point: Point, piece: Piece? = null) {
        board[point.x][point.y] = piece
    }

    fun forEach( fn: (point: Point, piece: Piece? ) -> Unit) {
        board.forEachIndexed { x, arr ->
            arr.forEachIndexed { y, piece ->
                fn(Point(x,y), piece)
            }
        }
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

