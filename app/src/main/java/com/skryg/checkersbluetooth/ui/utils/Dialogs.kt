package com.skryg.checkersbluetooth.ui.utils

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
private fun RequestDialog(onAccept: ()-> Unit, onDecline: ()-> Unit, rotated: Boolean = false, content: @Composable ()-> Unit){
    Dialog(onDismissRequest = onDecline){
        var modifier = Modifier.background(Color(0xB3FFFFFF), shape = RoundedCornerShape(16.dp)).padding(8.dp)
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
