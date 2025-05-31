package com.skryg.checkersbluetooth.bluetooth

import com.skryg.checkersbluetooth.game.logic.model.GameType
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Message

@Serializable
@SerialName("game_init")
data class GameInitMessage(
    val gameType: GameType,
    val localPlayerTurn: Turn,
    val playerName: String
) : Message()


@Serializable
@SerialName("game_init_ack")
data class GameInitAckMessage(
    val gameType: GameType,
    val localPlayerTurn: Turn,
    val playerName: String
) : Message()

@Serializable
@SerialName("move")
data class MoveMessage(
    val from: String,
    val to: String
) : Message()

@Serializable
@SerialName("draw")
data class DrawMessage(
    val playerName: String
) : Message()


@Serializable
@SerialName("resign")
data class ResignMessage(
    val playerName: String
) : Message()
