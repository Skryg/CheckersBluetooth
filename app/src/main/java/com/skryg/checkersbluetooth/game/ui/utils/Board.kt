package com.skryg.checkersbluetooth.game.ui.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skryg.checkersbluetooth.MainActivity

@Composable
fun Board(stateList: List<SquareUiState>) {


    Canvas(
        Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .border(1.dp, Color.Black)
        ) {
        val sz = size.width/8


        val theme = MainActivity.gameTheme
        for (i in 0 until 8) {
            for (j in 0 until 8) {
                val offset = Offset(i * sz, j * sz)
                drawSquare(stateList[i * 8 + j], offset, Size(sz, sz))
            }
        }
    }
}

fun DrawScope.drawSquare(uiState: SquareUiState, offset: Offset, sqSize: Size){
    val theme = MainActivity.gameTheme
    drawRect(color = if (uiState.isDarkSquare) theme.darkSquareColor  else theme.lightSquareColor,
        topLeft = offset,
        size = sqSize
    )
//    if(uiState.isSelected){
//        drawRect(
//            color = Color.Red,
//            alpha = 0.3f,
//            topLeft = offset,
//            size = sqSize
//        )
//    }
//    if(uiState.isMoveAvailable){
//        drawRect(
//            color = Color.Blue,
//            alpha = 0.3f,
//            topLeft = offset,
//            size = sqSize
//        )
//    }

    val squareCenter = offset + Offset(sqSize.width/2,sqSize.height/2)
    if(uiState.canMoveTo){
        drawCircle(
            color = Color.Green,
            radius = sqSize.width / 4,
            center = squareCenter
        )
    }
    uiState.piece?.let { piece ->
        drawCircle(
            color = if (piece.isDark) theme.darkPieceColor else theme.lightPieceColor,
            radius = sqSize.width / 2.3f,
            center = squareCenter
        )
        if (piece.isKing) {
            drawCircle(
                color = Color.Yellow,
                radius = sqSize.width / 4,
                center = squareCenter
            )
        }
    }
}

@Preview
@Composable
fun Test() {

    val list = ArrayList<SquareUiState>()
    for (i in 0 until 8) {
        for(j in 0 until 8) {
            val isDark = (i % 2 + j) % 2 != 0

            list.add(
                SquareUiState(
                    isDarkSquare = isDark
                )
            )
        }
    }
    list[2] = list[2].copy(piece = PieceUi(isDark = true))
    Column(Modifier.fillMaxSize()){
        Board(stateList = list)

    }

}