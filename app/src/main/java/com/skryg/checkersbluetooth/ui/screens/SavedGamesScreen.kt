package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.database.GameRepository
import com.skryg.checkersbluetooth.ui.AppViewModelProvider
import com.skryg.checkersbluetooth.ui.NavigationDestination
import com.skryg.checkersbluetooth.ui.utils.GameEntry
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
object SavedGamesDestination: NavigationDestination(
    route = "saved_games_screen",
    icon = Icons.Default.Menu,
    defaultTopBar = false,
    topBarContent = @Composable { TopAppBar(title = { Text("Saved Games") }) },
    name = "Saved Games"
)

@Composable
fun SavedGamesScreen(viewModel: SavedGamesViewModel
                     = viewModel(factory = AppViewModelProvider.Factory),
                     onGameClick: (GameEntity) -> Unit = {}){
    val games = viewModel.games
    SavedGamesList(games, onGameClick)
}

@Composable
fun SavedGamesList(games: List<GameEntity>? = null, onGameClick: (GameEntity)->Unit = {}){

    if(games == null) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            return
        }
    }

    LazyColumn {
        items(games!!){ game ->
            GameEntry(game, onGameClick)
        }
    }

}

@Preview
@Composable
fun SavedGamesScreenPreview() {
    val games = remember { mutableStateListOf<GameEntity>() }
    for(i in 0 until 10){
        games.add(GameEntity())
    }
    SavedGamesList(games)
}