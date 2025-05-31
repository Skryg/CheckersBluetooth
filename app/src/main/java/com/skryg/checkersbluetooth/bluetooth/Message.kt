package com.skryg.checkersbluetooth.bluetooth

import com.skryg.checkersbluetooth.game.logic.model.GameType
import com.skryg.checkersbluetooth.game.logic.model.Turn
import kotlinx.serialization.Serializable

@Serializable
sealed class Message(val type: MessageType)

@Serializable data class GameInitMessage(
    val gameType: GameType,
    val localPlayerTurn: Turn,
    val playerName: String
) : Message(MessageType.GAME_INIT)

@Serializable data class GameInitAckMessage(
    val gameType: GameType,
    val localPlayerTurn: Turn,
    val playerName: String
) : Message(MessageType.GAME_INIT_ACK)

@Serializable data class MoveMessage(
    val from: String,
    val to: String
) : Message(MessageType.MOVE)

@Serializable data class DrawMessage(
    val playerName: String
) : Message(MessageType.DRAW)

@Serializable data class ResignMessage(
    val playerName: String
) : Message(MessageType.RESIGN)

enum class MessageType {
    GAME_INIT,
    GAME_INIT_ACK,
    MOVE,
    DRAW,
    RESIGN
}

