package com.skryg.checkersbluetooth.database

import android.content.Context
import com.skryg.checkersbluetooth.game.services.GameController
import com.skryg.checkersbluetooth.game.services.GameControllerImpl

interface AppContainer {
    val gameController: GameController
    val gameRepository: GameRepository
//    var mainActivity: MainActivity
}

class AppDataContainer(context: Context): AppContainer {
    override val gameController by lazy {
        GameControllerImpl(gameRepository)
    }
    override val gameRepository by lazy {
        OfflineGameRepository(AppDatabase.getDatabase(context).gameDao())
    }

//    override var mainActivity: MainActivity = (MainActivity)context
}