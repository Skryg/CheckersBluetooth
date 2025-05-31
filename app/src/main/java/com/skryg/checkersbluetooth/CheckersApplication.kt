package com.skryg.checkersbluetooth

import android.app.Application
import android.media.SoundPool
import com.skryg.checkersbluetooth.database.AppContainer
import com.skryg.checkersbluetooth.database.AppDataContainer
import com.skryg.checkersbluetooth.sound.GameSounds
import com.skryg.checkersbluetooth.sound.GameSoundsImpl

class CheckersApplication: Application() {
    companion object {
        const val TAG = "CheckersApplication"
    }

    lateinit var container: AppContainer
    lateinit var gameSounds: GameSounds

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        gameSounds = GameSoundsImpl(this)

    }
}
