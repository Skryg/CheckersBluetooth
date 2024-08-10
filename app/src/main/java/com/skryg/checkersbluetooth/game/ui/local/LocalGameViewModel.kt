package com.skryg.checkersbluetooth.game.ui.local

import androidx.lifecycle.ViewModel
import com.skryg.checkersbluetooth.game.GameController
import com.skryg.checkersbluetooth.game.ui.utils.BoardUpdater
import com.skryg.checkersbluetooth.game.ui.utils.Point

class LocalGameViewModel(val gameController: GameController): ViewModel(), BoardUpdater {
    override fun updateSelected(point: Point?) {
        TODO("Not yet implemented")
    }

    override fun moveTo(point: Point) {
        TODO("Not yet implemented")
    }
}