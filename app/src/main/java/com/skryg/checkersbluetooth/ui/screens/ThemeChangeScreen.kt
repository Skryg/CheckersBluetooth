package com.skryg.checkersbluetooth.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skryg.checkersbluetooth.MainActivity
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.ui.theme.GameTheme
import com.skryg.checkersbluetooth.game.ui.utils.Board
import com.skryg.checkersbluetooth.game.ui.utils.LittleBoard
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import com.skryg.checkersbluetooth.game.ui.utils.UiState
import com.skryg.checkersbluetooth.ui.NavigationDestination

object ThemeChangeDestination: NavigationDestination(
    route = "theme_change_screen",
    name = "Theme change",
    defaultTopBar = true,
    defaultBottomBar = false,
    bottomBarContent = @Composable {
        BottomAppBar {
            val theme = MainActivity.gameTheme.observeAsState()
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                GameTheme.entries.forEach {
                    var modifier = Modifier.clickable{
                        MainActivity.gameTheme.value = it
                    }
                    if(theme.value == it) modifier = modifier.border(1.dp, Color.Green)

                    Column(modifier = modifier){
                        LittleBoard(modifier = Modifier.size(60.dp), theme = it)
                    }
                }
            }
        }
    },
)

@Composable
fun ThemeChangeScreen(){
    val theme = MainActivity.gameTheme.observeAsState()
    val pieces = ArrayList<PieceUi>()
    for(i in 0 until 2){
        for(j in 0 until 8){
            if((i + j)%2 == 1) pieces.add(PieceUi(isDark = true, point = Point(j,i)))
        }
    }
    for(i in 6 until 8){
        for(j in 0 until 8){
            if((i + j)%2 == 1) pieces.add(PieceUi(isDark = false, point = Point(j,i)))
        }
    }

    val state = remember{ mutableStateOf(UiState(pieces = pieces))}
    Board(state = state, theme = theme.value!!)

}