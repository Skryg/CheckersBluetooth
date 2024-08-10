package com.skryg.checkersbluetooth.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.game.ui.local.LocalGameViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LocalGameViewModel(checkersApplication().container.gameController)
        }
    }
}

fun CreationExtras.checkersApplication(): CheckersApplication{
    return this[AndroidViewModelFactory.APPLICATION_KEY] as CheckersApplication
}