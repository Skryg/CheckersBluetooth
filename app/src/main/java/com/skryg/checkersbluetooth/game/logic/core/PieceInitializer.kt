package com.skryg.checkersbluetooth.game.logic.core

import com.skryg.checkersbluetooth.game.ui.utils.PieceUi

interface PieceInitializer {
    fun initialize(): List<PieceUi>
}