package com.skryg.checkersbluetooth.game.logic.core

import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.coroutines.flow.StateFlow

interface StateStreamer {
    fun getState(): StateFlow<GameState>
    fun update()
    suspend fun setWin(color: Turn)
    suspend fun setDraw()

}