package com.skryg.checkersbluetooth.game.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.skryg.checkersbluetooth.game.services.BluetoothGameProvider
import com.skryg.checkersbluetooth.game.services.LocalGameProvider
import com.skryg.checkersbluetooth.game.ui.view.BluetoothGameViewModel
import com.skryg.checkersbluetooth.game.ui.view.LocalGameViewModel
import com.skryg.checkersbluetooth.game.ui.view.SavedGameViewModel
import com.skryg.checkersbluetooth.ui.checkersApplication

@Suppress("UNCHECKED_CAST")
class GameViewModelFactory(private val gameId: Long): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(LocalGameViewModel::class.java)) {
            val app = extras.checkersApplication()
            val gameSounds = app.gameSounds
            val localGameProvider = app.container.gameController.getGame(gameId) as LocalGameProvider
            return LocalGameViewModel(localGameProvider, gameSounds) as T
        }
        if(modelClass.isAssignableFrom(BluetoothGameViewModel::class.java)) {
            val app = extras.checkersApplication()
            val bluetoothGameProvider = app.container.gameController.getGame(gameId) as BluetoothGameProvider
            val gameSounds = app.gameSounds

            return BluetoothGameViewModel(bluetoothGameProvider, gameSounds) as T
        }

        if(modelClass.isAssignableFrom(SavedGameViewModel::class.java)) {
            val app = extras.checkersApplication()
            val repository = app.container.gameRepository
            val gameSounds = app.gameSounds

            return SavedGameViewModel(repository, gameId, gameSounds=gameSounds) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}