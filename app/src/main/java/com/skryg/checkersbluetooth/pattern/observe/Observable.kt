package com.skryg.checkersbluetooth.pattern.observe

interface Observable {
    fun register(observer: Observer)
}