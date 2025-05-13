package com.skryg.checkersbluetooth.game.logic.core

import com.skryg.checkersbluetooth.game.logic.model.GameState
import com.skryg.checkersbluetooth.game.logic.model.GameStateReadonly
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.coroutines.flow.StateFlow

interface BaseStateStreamer {
    fun getStateFlow(): StateFlow<GameStateReadonly>
    fun update()
}

interface StateStreamer: BaseStateStreamer {
    suspend fun setWin(color: Turn)
    suspend fun setDraw()
}