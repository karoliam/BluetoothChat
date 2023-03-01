package com.karoliinamultas.bluetoothchat.data

interface MessagesRepository {

    suspend fun insertMessage(message: Message)

    suspend fun updateMessage(message: Message)

    suspend fun deleteMessage(message: Message)


}