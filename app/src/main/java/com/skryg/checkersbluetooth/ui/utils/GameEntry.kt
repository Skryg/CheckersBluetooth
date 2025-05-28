package com.skryg.checkersbluetooth.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skryg.checkersbluetooth.database.GameEntity
import com.skryg.checkersbluetooth.game.logic.model.GameConnection
import com.skryg.checkersbluetooth.game.logic.model.GameResult
import com.skryg.checkersbluetooth.game.logic.model.Point
import com.skryg.checkersbluetooth.game.ui.utils.LittleBoard
import com.skryg.checkersbluetooth.game.ui.utils.PieceUi
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GameEntry(game: GameEntity, onClick:(GameEntity)->Unit = {}) {
    val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val formattedDate = sdf.format(Date(game.creationTime))

    val pieceOneDark = (game.winner == GameResult.BLACK_WON || game.winner == GameResult.DRAW)
    val pieceTwoDark = (game.winner == GameResult.BLACK_WON || game.winner == GameResult.ONGOING)
    val pieces = listOf(
        PieceUi(point= Point(0,1), isDark = pieceOneDark),
        PieceUi(point= Point(1,0), isDark = pieceTwoDark)
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable { onClick(game) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer

        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LittleBoard(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                pieceList = pieces
            )

            Column(
                modifier = Modifier.weight(0.8f)
                    .padding(start=16.dp)
            ) {
                Text(
                    text = if (game.gameConnection == GameConnection.BLUETOOTH)
                        "Bluetooth Game" else "Local Game",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Created: $formattedDate",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = when (game.winner){
                    GameResult.WHITE_WON -> "White won"
                    GameResult.BLACK_WON -> "Black won"
                    GameResult.DRAW -> "Draw"
                    else -> "Ongoing"
                },
//                color = if (game.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF9800),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }
    }
    
    
}

@Preview
@Composable
fun GameEntryPreview() {
    GameEntry(
        GameEntity(winner = GameResult.DRAW)
    )
}