package com.skryg.checkersbluetooth

import android.app.Application
import com.skryg.checkersbluetooth.database.AppContainer
import com.skryg.checkersbluetooth.database.AppDataContainer

class CheckersApplication: Application() {
    private lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}