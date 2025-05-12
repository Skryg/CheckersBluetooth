package com.skryg.checkersbluetooth.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.skryg.checkersbluetooth.CheckersApplication
import com.skryg.checkersbluetooth.ui.screens.SavedGamesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SavedGamesViewModel(checkersApplication().container.gameRepository)
        }
    }
}

fun CreationExtras.checkersApplication(): CheckersApplication{
    return this[AndroidViewModelFactory.APPLICATION_KEY] as CheckersApplication
}