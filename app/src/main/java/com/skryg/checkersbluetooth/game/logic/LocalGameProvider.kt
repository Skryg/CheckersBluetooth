package com.skryg.checkersbluetooth.game.logic

import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.Point
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocalGameProvider: GameProvider {
    private val _stateFlow = MutableStateFlow(GameState())

    data class BoardPiece(val isDark: Boolean = false, val isKing: Boolean = false)

    private val board: Array<Array<BoardPiece?>> = Array(8){Array(8){null}}
    private var charge: Point? = null

    init {
        val piecesList = ArrayList<PieceUi>()
        for (i in 0 until 2) {
            for (j in 0 until 8) {
                if ((i + j) % 2 != 0){
                    board[i][j] = BoardPiece(isDark = true)
                    piecesList.add(PieceUi(isDark = true, point = Point(j,i)))
                }
            }
        }
        for (i in 6 until 8) {
            for (j in 0 until 8) {
                if ((i + j) % 2 != 0){
                    board[i][j] = BoardPiece(isDark = false)
                    piecesList.add(PieceUi(isDark = false, point = Point(j,i)))
                }
            }
        }
        _stateFlow.update{
            it.copy(pieces = piecesList)
        }

    }

    override fun gameStateFlow(): StateFlow<GameState> = _stateFlow.asStateFlow()

    //get all possible attackers
    private fun getAttackers(): List<Point>{
        val state = _stateFlow.value
        val attacks = state.pieces.filter {
            it.isDark == state.turn && getAttacks(it.point).isNotEmpty()
        }.map { it.point }
        return attacks
    }

    //get attacks for one point
    private fun getAttacks(point: Point): List<Point>{
        if(charge != null && charge != point) return emptyList()

        val (y,x) = point
        val piece = board[x][y] ?: return emptyList()

        val allSites: (Int) -> List<Point> = { depth ->
            val list = ArrayList<Point>()
            for (i in intArrayOf(-1,1)){
                for (j in intArrayOf(-1,1)){
                    val offset = Point(i,j)
                    list.addAll(checkRecursive(point+offset, offset, piece.isDark, false, depth))
                }
            }
            list
        }

        val twoSitesPawn: () -> List<Point> = {
            val list = ArrayList<Point>()
            val j = if(piece.isDark) 1 else -1
            for (i in intArrayOf(-1,1)){
                val offset = Point(i,j)
                list.addAll(checkRecursive(point+offset, offset, piece.isDark, false, 2))
            }
            list
        }

        if(piece.isKing) return allSites(10)
        if(charge != null) return allSites(2)
        return twoSitesPawn()
    }

    private fun checkRecursive(
        point: Point,
        offset: Point,
        color: Boolean,
        jumpOver: Boolean,
        depth: Int = 2
    ): List<Point>{
        if(depth==0) return emptyList()
        val (y,x) = point
        val (dy,dx) = offset
        if(x !in 0..7 || y !in 0..7) return emptyList()
        val piece = board[x][y]
        if(piece == null){
            val list = checkRecursive(Point(y+dy, x+dx), offset, color, jumpOver, depth-1)
            if(jumpOver) return list + point
            return list
        }
        if(piece.isDark == color) return emptyList()
        if(!jumpOver){
            return checkRecursive(Point(y+dy, x+dx), offset, color, true, depth-1)
        }
        return emptyList()
    }

    private fun getNotAttackMoves(point: Point): List<Point> {
        val (y,x) = point
        val piece = board[x][y] ?: return emptyList()
        val list = ArrayList<Point>()

        if(piece.isKing){
            for(i in intArrayOf(-1,1)){
                for(j in intArrayOf(-1,1)){
                    val offset = Point(i,j)
                    list.addAll(checkRecursive(point+offset, offset, piece.isDark, true, 10))
                }
            }
            return list
        }
        val offset = if(piece.isDark) 1 else -1


        list.addAll(checkRecursive(point+Point(1,offset), Point(1,offset), piece.isDark, true, 1))
        list.addAll(checkRecursive(point+Point(-1,offset), Point(-1,offset), piece.isDark, true, 1))
        return list
    }

    private fun getNoAttackMovables(): List<Point>{
        val state = _stateFlow.value
        return state.pieces.filter {
            it.isDark == state.turn && getNotAttackMoves(it.point).isNotEmpty()
        }.map{it.point}
    }

    override fun makeMove(from: Point, to: Point) {
        val (y1,x1) = from
        val (y2,x2) = to
        if(board[x2][y2] != null) throw GameProvider.InvalidMoveException()
        if(board[x1][y1] == null || board[x1][y1]!!.isDark != _stateFlow.value.turn)
            throw GameProvider.InvalidMoveException()

        val attacks = getAttacks(from)
        if(to in attacks){

            val offset = (to-from)/(to-from).col
            var f = from+offset
            while(f!=to){
                val (y,x) = f
                val piece = board[x][y]
                if(piece != null){
                    _stateFlow.update{
                        it.copy(pieces = it.pieces.filterNot { piece -> piece.point == f })
                    }
                    board[x][y]=null
                }
                f+=offset
            }

            board[x2][y2] = board[x1][y1]
            board[x1][y1] = null
            charge = to
            if(getAttacks(to).isEmpty()){
                charge = null
                _stateFlow.update {
                    it.copy(turn = !it.turn)
                }
            }

            _stateFlow.update{
                it.copy(pieces = it.pieces.map{ piece ->
                    if(piece.point == from) piece.copy(point = to)
                    else piece
                })
            }
        }
        else if(to in getNotAttackMoves(from)){
            board[x2][y2] = board[x1][y1]
            board[x1][y1] = null

            _stateFlow.update{
                it.copy(turn = !it.turn, pieces = it.pieces.map{ piece ->
                    if(piece.point == from) piece.copy(point = to)
                    else piece
                })
            }
        }
        else throw GameProvider.InvalidMoveException()

    }

    override fun calculateMoves(point: Point): List<Point> {
        val attacks = getAttacks(point)
        if(attacks.isNotEmpty()) return attacks
        return getNotAttackMoves(point)
    }

    override fun getMovables(): List<Point> {
        val attackers = getAttackers()
        if (attackers.isNotEmpty()) return attackers
        return getNoAttackMovables()

    }

}