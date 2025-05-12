package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.database.GameRepository
import kotlinx.coroutines.launch
import java.util.logging.Logger

class SavedGamesViewModel(repository: GameRepository): ViewModel() {
    private val gamesState = mutableStateOf(null as List<GameEntity>?)
    init {
        viewModelScope.launch {
            Logger.getLogger("SavedGamesViewModel").info("Collecting games")
            repository.getAllGamesFlow().collect { games ->
                gamesState.value = games
            }
        }
    }

    val games: List<GameEntity>?
        get() = gamesState.value
}