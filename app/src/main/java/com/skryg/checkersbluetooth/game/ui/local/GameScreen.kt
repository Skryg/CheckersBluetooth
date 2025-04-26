package com.skryg.checkersbluetooth.game.ui.local

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skryg.checkersbluetooth.R
import com.skryg.checkersbluetooth.game.logic.model.Turn
import com.skryg.checkersbluetooth.game.ui.utils.Board
import com.skryg.checkersbluetooth.game.ui.utils.UiState
import com.skryg.checkersbluetooth.ui.AppViewModelProvider
import com.skryg.checkersbluetooth.ui.NavigationDestination
import com.skryg.checkersbluetooth.ui.utils.DrawRequestDialog
import com.skryg.checkersbluetooth.ui.utils.ResignDialog

object LocalGameDestination: NavigationDestination(
    route = "local_game",
    name = "Local Game",
    defaultTopBar = true,
    defaultBottomBar = false,
)



@Composable
fun LocalGameScreen(viewModel: LocalGameViewModel = viewModel(factory = AppViewModelProvider.Factory)){
    Column(Modifier.fillMaxSize()){
        val state = viewModel.gameUiState.collectAsState()
        val playerState2 = PlayerState("Black", state.value.turn == Turn.BLACK)
        val playerState1 = PlayerState("White", state.value.turn == Turn.WHITE)
        LocalGameButtons(Modifier,{}, {},playerState2, rotated=true)
        Board(modifier = Modifier.weight(1f),state = state, boardUpdater = viewModel)
        LocalGameButtons(Modifier,{},{}, playerState1, rotated = false)
    }
}



@Composable
fun LocalGameButtons(modifier:Modifier=Modifier,
                     onClickDraw: ()->Unit, onClickResign: ()-> Unit,
                     playerState: PlayerState,
                     rotated: Boolean = false ){
    var modif = modifier.fillMaxWidth()
    if(rotated) modif = modif.rotate(180f)

    var drawDialog by remember{mutableStateOf(false)}
    var resignDialog by remember{mutableStateOf(false)}

    if(drawDialog) DrawRequestDialog(
        onAccept = { onClickDraw(); drawDialog = false },
        onDecline = { drawDialog = false },
        rotated = !rotated)

    if(resignDialog) ResignDialog(
        onAccept = { onClickResign(); resignDialog = false },
        onDecline = { resignDialog = false },
        rotated = rotated
    )

    Row(modifier = modif,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically){
        Row {
            IconButton(onClick = { resignDialog = true } ){
                val flagIcon = R.drawable.baseline_flag_24
                Icon(painter = painterResource(id = flagIcon), contentDescription = "Give up")
            }
            IconButton(onClick = { drawDialog = true } ){
                val handsIcon = R.drawable.baseline_handshake_24
                Icon(painter = painterResource(id = handsIcon), contentDescription = "Propose draw")
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



@Preview
@Composable
fun LocalGameScreenPreview(viewModel: LocalGameViewModel = viewModel(factory = AppViewModelProvider.Factory)){
    Column(Modifier.fillMaxSize()){
        val playerState1 = remember { mutableStateOf(PlayerState(name = "Default")) }
        val playerState2 = remember { mutableStateOf(PlayerState(name = "Default", turn = false)) }

        LocalGameButtons(Modifier,{}, {},playerState1.value, rotated=true)
        val state = remember { mutableStateOf(UiState()) }
        Board(modifier = Modifier.weight(1f),state = state, boardUpdater = viewModel)
        LocalGameButtons(Modifier,{},{}, playerState2.value, rotated = false)
    }
}

