package com.skryg.checkersbluetooth.game.logic.exception

class GameLoadException(override val message: String?= "", override val cause: Throwable?)
    : Exception(message, cause)

class GameNotFoundException: Exception()
class GameEndedException : Exception()