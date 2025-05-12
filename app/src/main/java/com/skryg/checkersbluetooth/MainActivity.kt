package com.skryg.checkersbluetooth

import android.content.Context
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.skryg.checkersbluetooth.game.ui.theme.GameTheme
import com.skryg.checkersbluetooth.ui.Navigation
import com.skryg.checkersbluetooth.ui.theme.CheckersBluetoothTheme

class MainActivity : ComponentActivity() {
    companion object{
        var gameTheme = MutableLiveData(GameTheme.Default)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val sharedPrefs = this.getPreferences(Context.MODE_PRIVATE)
        val themeStr = getString(R.string.game_theme)
        GameTheme.entries[sharedPrefs.getInt(themeStr,0)].let {
            gameTheme.value = it
        }
        gameTheme.observe(this){
            sharedPrefs.edit().putInt(themeStr,it.ordinal).apply()
        }

        setContent {
            CheckersBluetoothTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation()
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheckersBluetoothTheme {
        Navigation()
    }
}