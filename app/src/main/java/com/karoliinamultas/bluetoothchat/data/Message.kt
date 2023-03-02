package com.karoliinamultas.bluetoothchat.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "messages")
data class Message(
    @PrimaryKey
    val message_uuid: String,
    val message_content: String,
    val chat_id: String,
    val local_message: Boolean,
)
