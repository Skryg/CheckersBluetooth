package com.skryg.checkersbluetooth.database

import android.content.Context

interface AppContainer {
    val gameRepository: GameRepository
}

class AppDataContainer(context: Context): AppContainer {
    override val gameRepository by lazy {
        OfflineGameRepository(AppDatabase.getDatabase(context).gameDao())
    }
}