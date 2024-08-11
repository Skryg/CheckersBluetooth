package com.skryg.checkersbluetooth.database

import android.content.Context
import com.skryg.checkersbluetooth.game.GameController
import com.skryg.checkersbluetooth.game.GameControllerImpl

interface AppContainer {
    val gameController: GameController
    val gameRepository: GameRepository
}

class AppDataContainer(context: Context): AppContainer {
    override val gameController: GameController
        get() = GameControllerImpl()
    override val gameRepository by lazy {
        OfflineGameRepository(AppDatabase.getDatabase(context).gameDao())
    }
}