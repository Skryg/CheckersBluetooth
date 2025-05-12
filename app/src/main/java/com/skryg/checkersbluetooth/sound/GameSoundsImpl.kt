package com.skryg.checkersbluetooth.sound

import android.content.Context
import android.media.SoundPool
import java.util.TreeMap

class GameSoundsImpl(private val applicationContext: Context): GameSounds {
    private val map = TreeMap<Sound, Int>()
    private val soundPool: SoundPool

    init {
        val soundAttributes = android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_GAME)
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(soundAttributes)
            .build()
    }

    override fun play(sound: Sound): Int {
        val id = map[sound]
        return soundPool.play(id ?: return 0, 1f, 1f, 0, 0, 1f)
    }

    override fun load(sound: Sound) {
        val id = soundPool.load(applicationContext, sound.res, 1)
        map[sound] = id
    }

    override fun unload(sound: Sound) {
        val id = map[sound]
        if (id != null) {
            soundPool.unload(id)
            map.remove(sound)
        }
    }
}