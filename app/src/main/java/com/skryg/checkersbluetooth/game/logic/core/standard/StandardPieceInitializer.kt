package com.skryg.checkersbluetooth.game.logic.core.standard

import com.skryg.checkersbluetooth.game.logic.core.PieceInitializer
import com.skryg.checkersbluetooth.game.logic.model.Piece
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import java.util.ArrayList

class StandardPieceInitializer: PieceInitializer {
    override fun initialize(): List<PieceUi> {
        val array = ArrayList<PieceUi>()
        for (y in 0 until 2) {
            for (x in 0 until 8) {
                if ((x + y) % 2 != 0){
                    array.add(PieceUi(isDark=true, isKing=false, Point(x,y)))
                }
            }
        }
        for (y in 6 until 8) {
            for (x in 0 until 8) {
                if ((x + y) % 2 != 0){
                    array.add(PieceUi(isDark=false, isKing=false, Point(x,y)))
                }
            }
        }
        return array
    }
}