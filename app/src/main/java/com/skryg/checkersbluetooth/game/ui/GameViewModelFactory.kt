package com.skryg.checkersbluetooth.game.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.skryg.checkersbluetooth.game.ui.local.LocalGameViewModel
import com.skryg.checkersbluetooth.ui.checkersApplication

@Suppress("UNCHECKED_CAST")
class GameViewModelFactory(private val gameId: Long): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(LocalGameViewModel::class.java)) {
            val gameController = extras.checkersApplication().container.gameController
            return LocalGameViewModel(gameController, gameId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}