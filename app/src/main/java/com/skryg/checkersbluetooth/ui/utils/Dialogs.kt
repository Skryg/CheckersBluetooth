package com.skryg.checkersbluetooth.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import kotlinx.coroutines.delay

@Composable
private fun RequestDialog(onAccept: ()-> Unit, onDecline: ()-> Unit, rotated: Boolean = false, content: @Composable ()-> Unit){
    Dialog(onDismissRequest = onDecline){
        var modifier = Modifier
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
        if(rotated) modifier = modifier.rotate(180f)
        Column(modifier){
            Column(Modifier.padding(16.dp)){
                content()
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                IconButton(onClick = onAccept) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Accept")
                }
                IconButton(onClick = onDecline){
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Decline")
                }
            }
        }
    }
}

@Composable
fun DrawRequestDialog(onAccept: ()-> Unit, onDecline: ()-> Unit, rotated: Boolean = false){
    RequestDialog(onAccept, onDecline, rotated){
        Text(text = "Do you want to draw?")
    }
}

@Composable
fun ResignDialog(onAccept: () -> Unit, onDecline: () -> Unit, rotated: Boolean = false){
    RequestDialog(onAccept, onDecline, rotated){
        Text(text = "Do you want to resign?")
    }
}

@Composable
fun GameOverDialog(result: GameResult, newGame: ()->Unit = {}, goMenu: ()-> Unit = {}){

    Dialog(onDismissRequest = goMenu){
        val modifier = Modifier
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally){
               Text(text = "Game over", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                when (result) {
                    GameResult.WHITE_WON -> {
                        Text(text = "White won")
                    }
                    GameResult.BLACK_WON -> {
                        Text(text = "Black won")
                    }
                    else -> {
                        Text(text = "Draw")
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){
                TextButton(onClick = newGame) {
                    Text("New game")
                }
                TextButton(onClick = goMenu){
                    Text("Go to menu")
                }
            }
        }
    }
}

@Preview
@Composable
fun DrawRequestDialogPreview(){
    GameOverDialog(GameResult.WHITE_WON) { }
}


@Composable
fun OnceButton(onClick: () -> Unit,
               modifier: Modifier = Modifier,
               enabled: Boolean = true,
               countdownMillis: Long = 1000L,
               content: @Composable RowScope.() -> Unit
) {
    var double by remember { mutableStateOf(true) }
    LaunchedEffect(double) {
        if(!double) {
            delay(countdownMillis)
            double = true
        }
    }

    Button(
        onClick = {
            double = false
            onClick()
        },
        enabled = enabled,
        modifier = modifier,
        content = content
    )

}
