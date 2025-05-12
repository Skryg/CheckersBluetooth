package com.skryg.checkersbluetooth.sound

import androidx.annotation.IdRes
import com.skryg.checkersbluetooth.R

interface GameSounds {
    fun play(sound: Sound): Int
    fun load(sound: Sound)
    fun unload(sound: Sound)
}

enum class Sound(@IdRes val res: Int) {
    MOVE(R.raw.move),
    WIN(R.raw.win),
    LOSE(R.raw.lose)
}