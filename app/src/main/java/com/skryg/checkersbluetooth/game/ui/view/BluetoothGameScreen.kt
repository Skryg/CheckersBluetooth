package com.skryg.checkersbluetooth.game.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.navArgument
import com.skryg.checkersbluetooth.R
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.ui.GameViewModelFactory
import com.skryg.checkersbluetooth.game.ui.utils.Board
import com.skryg.checkersbluetooth.ui.NavigationDestination
import com.skryg.checkersbluetooth.ui.utils.DrawRequestDialog
import com.skryg.checkersbluetooth.ui.utils.GameOverDialog
import com.skryg.checkersbluetooth.ui.utils.ResignDialog

object BluetoothGameDestination: NavigationDestination(
    route = "bluetooth_game/{game_id}",
    arguments = listOf(navArgument(name = "game_id") {
        type = androidx.navigation.NavType.LongType
    }),
    name = "Bluetooth Game",
    defaultTopBar = true,
    defaultBottomBar = false,
)

@Composable
fun BluetoothGameScreen(gameId: Long, goMenu: () -> Unit){
    val viewModel: BluetoothGameViewModel = viewModel(factory = GameViewModelFactory(gameId))
    val localTurn = viewModel.localTurn
    Column(Modifier.fillMaxSize()){
        val state by viewModel.gameUiState.collectAsStateWithLifecycle()

        state.result.let {
            if (it != GameResult.ONGOING) {
                GameOverDialog(it, goMenu = goMenu)
            }
        }

        val resign = {
            viewModel.resign()
        }

        val playerState2 = PlayerState(viewModel.opponentName, state.turn != localTurn)
        val playerState1 = PlayerState(viewModel.myName, state.turn == localTurn)
        val boardModifier = if(localTurn == Turn.WHITE) Modifier.weight(1f)
                            else Modifier.weight(1f).rotate(180f)
        BluetoothGameButtons(Modifier, playerState2, showButtons = false)
        Board(modifier = boardModifier,state = state, boardUpdater = viewModel)
        BluetoothGameButtons(Modifier, playerState1, resign, rotated = false)
    }
}

@Composable
fun BluetoothGameButtons(
    modifier:Modifier=Modifier,
    playerState: PlayerState,
    onClickResign: ()-> Unit ={},
    showButtons: Boolean = true,
    rotated: Boolean = false
){
    var modif = modifier.fillMaxWidth()
    if(rotated) modif = modif.rotate(180f)

    var resignDialog by remember{ mutableStateOf(false) }

    if(resignDialog) ResignDialog(
        onAccept = { onClickResign(); resignDialog = false },
        onDecline = { resignDialog = false },
        rotated = rotated
    )

    Row(modifier = modif,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        Row {
            if(showButtons) {
                IconButton(onClick = { resignDialog = true }) {
                    val flagIcon = R.drawable.baseline_flag_24
                    Icon(painter = painterResource(id = flagIcon), contentDescription = "Give up")
                }
//
            }
        }
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            val icon = painterResource(id = R.drawable.baseline_games_24)
            if(playerState.turn)
                Icon(painter = icon, contentDescription = "Turn", tint = Color.Green)
            Spacer(modifier = Modifier.width(16.dp))
            Text(playerState.name)
        }

    }
}
