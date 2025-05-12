package com.skryg.checkersbluetooth.game.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.game.logic.model.GameConnection
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.Piece
import com.skryg.checkersbluetooth.game.ui.GameViewModelFactory
import com.skryg.checkersbluetooth.game.ui.utils.Board
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.UiState
import com.skryg.checkersbluetooth.ui.NavigationDestination
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SavedGameDestination: NavigationDestination(
    route = "saved_game/{game_id}",
    arguments = listOf(navArgument(name = "game_id") {
        type = androidx.navigation.NavType.LongType
    }),
    name = "Saved Game",
    defaultTopBar = true,
    defaultBottomBar = false,
)

@Composable
fun ShowSavedGame(gameId: Long){
    val viewModel: SavedGameViewModel = viewModel(factory = GameViewModelFactory(gameId))

    if(viewModel.game == null) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val pieceState = viewModel.movesFlow.collectAsState().value
    val buttonsData = ButtonsData(
        prevOnClick = { viewModel.movePrev() },
        nextOnClick = { viewModel.moveNext() },
        minMove = 0,
        maxMove = pieceState.maxMoves,
        movePointer = pieceState.movePointer
    )

    ShowSavedGameContent(viewModel.game!!, pieceState.pieces, buttonsData)
}

@Composable fun ShowSavedGameContent(game: GameEntity, pieces: List<PieceUi>, buttonsData: ButtonsData) {

    val result = when(game.winner) {
        GameResult.BLACK_WON -> "Black Won"
        GameResult.WHITE_WON -> "White Won"
        GameResult.DRAW -> "Draw"
        GameResult.ONGOING -> "Ongoing"
    }

    val gameType = when(game.gameConnection) {
        GameConnection.LOCAL -> "Local"
        GameConnection.BLUETOOTH -> "Bluetooth"
    }

    Column {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(label = "Game Type", value = gameType)
            InfoRow(label = "Result", value = result)
        }

        Board(modifier = Modifier.weight(1f), state = UiState(pieces = pieces))
        Buttons(buttonsData)
    }

}

data class ButtonsData(
    val prevOnClick: () -> Unit ={},
    val nextOnClick: () -> Unit ={},
    val minMove: Int = 0,
    val maxMove: Int = 0,
    var movePointer: Int = 0,
)

@Composable fun Buttons(data: ButtonsData){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = data.prevOnClick,
            enabled = data.movePointer > data.minMove
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous")
            Spacer(Modifier.width(8.dp))
            Text("Previous")
        }

        Text(
            text = "Move ${data.movePointer} of ${data.maxMove}",
            style = MaterialTheme.typography.bodyLarge
        )

        Button(
            onClick = data.nextOnClick,
            enabled = data.movePointer < data.maxMove
        ) {
            Spacer(Modifier.width(8.dp))
            Text("Next")
            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next")

        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}