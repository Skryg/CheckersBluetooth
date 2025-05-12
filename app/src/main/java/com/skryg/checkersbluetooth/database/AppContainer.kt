package com.skryg.checkersbluetooth.database

import android.content.Context
import android.media.SoundPool
import com.skryg.checkersbluetooth.R
import com.skryg.checkersbluetooth.game.services.GameController
import com.skryg.checkersbluetooth.game.services.GameControllerImpl

interface AppContainer {
    val gameController: GameController
    val gameRepository: GameRepository
}

class AppDataContainer(context: Context): AppContainer {
    override val gameController by lazy {
        GameControllerImpl(gameRepository)
    }
    override val gameRepository by lazy {
        OfflineGameRepository(AppDatabase.getDatabase(context).gameDao())
    }
}