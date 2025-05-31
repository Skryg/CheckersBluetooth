package com.skryg.checkersbluetooth.bluetooth

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

class MessageSerializer {
    private val module = SerializersModule {
        polymorphic(Message::class) {
            subclass(GameInitMessage::class)
            subclass(GameInitAckMessage::class)
            subclass(MoveMessage::class)
            subclass(DrawMessage::class)
            subclass(ResignMessage::class)
        }
    }

    private val json = Json {
        serializersModule = module
        classDiscriminator = "type"  // tells the deserializer which field indicates the type
        ignoreUnknownKeys = true
    }

    fun serialize(message: Message): String {
        return json.encodeToString(message)
    }
    fun deserialize(jsonString: String): Message {
        return json.decodeFromString(jsonString)
    }
}