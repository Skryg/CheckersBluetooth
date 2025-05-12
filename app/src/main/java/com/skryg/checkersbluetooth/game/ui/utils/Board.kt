package com.skryg.checkersbluetooth.game.ui.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skryg.checkersbluetooth.MainActivity
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.ui.theme.GameTheme
import kotlinx.coroutines.runBlocking
import java.util.logging.Logger


@Composable
fun Board(modifier:Modifier=Modifier,
          state: UiState,
          boardUpdater: BoardUpdater? = null,
          theme: GameTheme = MainActivity.gameTheme.value!!
) {
    var point by remember { mutableStateOf(null as Point?) }
    val stateWrapper = remember { StateWrapper(state) }
    stateWrapper.value = state

    Canvas(
        modifier
            .fillMaxSize()
            .background(theme.backgroundColor)
            .aspectRatio(1f)
            .border(1.dp, Color.Black)
            .pointerInput(Unit){
                val sz = size.width/8
                detectTapGestures(
                    onTap = {
                        val newPoint = Point(it.x.div(sz).toInt(), it.y.div(sz).toInt())

                        if(newPoint in stateWrapper.value.movePoints)
                            point?.let {
                                runBlocking {
                                    Logger.getLogger("Board").info("Moving $it to $newPoint")
                                    boardUpdater?.move(point!!, newPoint)
                                }
                            }

                        point = if(newPoint == point) null else newPoint
                        boardUpdater?.updateSelected(point)
                    })
            }
        ) {
        val sz = size.width / 8
        fun Point.toOffset() = Offset(x * sz, y * sz)

        for (i in 0 until 8) {
            for (j in 0 until 8) {
                val offset = Offset(i * sz, j * sz)
                drawSquare(isDark = (i % 2 + j) % 2 != 0, offset, Size(sz, sz), theme)
            }
        }
        state.pieces.forEach { piece ->
            drawPiece(piece.isDark, piece.isKing, piece.point.toOffset(), Size(sz, sz), theme)
        }
        point?.let { drawSelect(it.toOffset(), Size(sz, sz)) }
        state.movePoints.forEach { drawMoveOption(it.toOffset(), Size(sz, sz)) }
        state.canMove.forEach { drawMovable(it.toOffset(), Size(sz, sz)) }
    }
}

private data class StateWrapper(
    var value: UiState = UiState()
)

@Composable
fun LittleBoard(modifier: Modifier = Modifier,
                theme: GameTheme = MainActivity.gameTheme.value!!,
                pieceList :List<PieceUi> = listOf(
                    PieceUi(point = Point(0,1)),
                    PieceUi(point = Point(1,0), isDark = true))){
    Canvas(modifier){
        val sz = size.width/2
        fun Point.toOffset() = Offset(x * sz, y * sz)
        for (i in 0 until 2){
            for (j in 0 until 2){
                val offset = Offset(i * sz, j * sz)
                drawSquare(isDark = (i % 2 + j) % 2 != 0, offset, Size(sz, sz), theme)
            }

            pieceList.forEach() { piece ->
                drawPiece(piece.isDark, piece.isKing, piece.point.toOffset(), Size(sz, sz), theme)
            }
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
                         theme: GameTheme = MainActivity.gameTheme.value!!
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
                        theme: GameTheme = MainActivity.gameTheme.value!!
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
    Column(Modifier.fillMaxSize().background(MainActivity.gameTheme.value!!.backgroundColor)){
        val state = remember { mutableStateOf(UiState())}
        Board(state=state.value)

    }
}

@Preview
@Composable
fun LittleTest(){
    Column{
        LittleBoard(modifier = Modifier.size(200.dp), theme = GameTheme.Blue)
    }
}