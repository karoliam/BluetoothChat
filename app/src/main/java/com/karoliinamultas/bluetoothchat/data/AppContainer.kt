package com.karoliinamultas.bluetoothchat.data

import android.content.Context

interface AppContainer {
    val messagesRepository: MessagesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val messagesRepository: MessagesRepository by lazy {
        OfflineMessagesRepository(BluetoothChatDatabase.getDatabase(context).messageDao())
    }
}