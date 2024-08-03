package com.skryg.checkersbluetooth.game.ui.utils

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.skryg.checkersbluetooth.MainActivity
import com.skryg.checkersbluetooth.game.ui.theme.GameTheme

@Composable
fun Board(state: State<UiState>, boardUpdater: BoardUpdater? = null) {
    var point by remember { mutableStateOf(null as Point?) }
    val uiState = state.value

    Canvas(
        Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .border(1.dp, Color.Black)
            .pointerInput(Unit){
                val sz = size.width/8
                detectTapGestures(
                    onTap = {
                        point = Point(it.x.div(sz).toInt(), it.y.div(sz).toInt())
                        boardUpdater?.let{
                            if(point in uiState.movePoints) {
                                boardUpdater.moveTo(point!!)
                            }
                            boardUpdater.updateSelected(point!!)
                        }

                    })
            }
        ) {
        val sz = size.width/8
        fun Point.toOffset() = Offset(col * sz, row * sz)

        for (i in 0 until 8) {
            for (j in 0 until 8) {
                val offset = Offset(i * sz, j * sz)
                drawSquare(isDark = (i % 2 + j) % 2 != 0, offset, Size(sz, sz))
            }
        }
        uiState.pieces.forEach { piece ->
            drawPiece(piece.isDark, piece.isKing, piece.point.toOffset(), Size(sz, sz))
        }
        point?.let{ drawSelect(it.toOffset(), Size(sz, sz)) }
        uiState.movePoints.forEach { drawMoveOption(it.toOffset(), Size(sz, sz)) }
        uiState.canMove.forEach { drawMovable(it.toOffset(), Size(sz, sz)) }
    }
}

@Composable
fun LittleBoard(modifier: Modifier = Modifier,theme: GameTheme){
    Canvas(modifier){
        val sz = size.width/2
        fun Point.toOffset() = Offset(col * sz, row * sz)
        for (i in 0 until 2){
            for (j in 0 until 2){
                val offset = Offset(i * sz, j * sz)
                drawSquare(isDark = (i % 2 + j) % 2 != 0, offset, Size(sz, sz), theme)
            }
            drawPiece(
                isDark = false,
                isKing = false,
                offset = Point(0,1).toOffset(),
                sqSize = Size(sz, sz),
                theme = theme
            )

            drawPiece(true,
                isKing = false,
                offset = Point(1,0).toOffset(),
                sqSize = Size(sz, sz),
                theme = theme
            )
        }
    }
}

private fun DrawScope.drawSelect(offset: Offset, sqSize: Size){
    drawRect(color = Color.Red,
        topLeft = offset,
        size = sqSize,
        alpha = 0.3f)
}

private fun DrawScope.drawSquare(isDark: Boolean,
                         offset: Offset, sqSize: Size,
                         theme: GameTheme = MainActivity.gameTheme
) {
    drawRect(
        color = if (isDark) theme.darkSquareColor else theme.lightSquareColor,
        topLeft = offset,
        size = sqSize
    )
}

private fun DrawScope.drawPiece(isDark: Boolean,
                        isKing: Boolean,
                        offset: Offset,
                        sqSize: Size,
                        theme: GameTheme = MainActivity.gameTheme
){
    val squareCenter = offset + Offset(sqSize.width/2,sqSize.height/2)

    drawCircle(
        color = if (isDark) theme.darkPieceColor else theme.lightPieceColor,
        radius = sqSize.width / 2.3f,
        center = squareCenter
    )
    if (isKing) {
        drawCircle(
            color = Color.Yellow,
            radius = sqSize.width / 4,
            center = squareCenter
        )
    }

}

private fun DrawScope.drawMovable(offset: Offset, sqSize: Size){
    drawRect(
        color = Color.Blue,
        alpha = 0.3f,
        topLeft = offset,
        size = sqSize
    )
}

private fun DrawScope.drawMoveOption(offset: Offset, sqSize: Size){
    val squareCenter = offset + Offset(sqSize.width/2,sqSize.height/2)
    drawCircle(
        color = Color.Green,
        radius = sqSize.width / 4,
        center = squareCenter
    )
}


@Preview
@Composable
fun Test() {
    Column(Modifier.fillMaxSize().background(MainActivity.gameTheme.backgroundColor)){
        val state = remember { mutableStateOf(UiState())}
        Board(state)

    }
}

@Preview
@Composable
fun LittleTest(){
    Column(){
        LittleBoard(modifier = Modifier.size(200.dp), theme = GameTheme.Blue)
    }
}