package com.karoliinamultas.bluetoothchat.data

import kotlinx.coroutines.flow.Flow

interface MessagesRepository {

    suspend fun insertMessage(message: Message)

    suspend fun updateMessage(message: Message)

    suspend fun deleteMessage(message: Message)

    fun getChatMessages(chatId: String): Flow<List<Message>>

    suspend fun deleteAllChatMessages()

    suspend fun deleteSingleChatMessages(chatId: String)

    suspend fun deleteOtherChatMessages(chatId: String)
}