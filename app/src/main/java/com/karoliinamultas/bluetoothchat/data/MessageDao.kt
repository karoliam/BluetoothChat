package com.karoliinamultas.bluetoothchat.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(message: Message)

    @Update
    suspend fun update(message: Message)

    @Delete
    suspend fun delete(message: Message)

    @Query("SELECT * FROM messages")
    fun getChatMessages(): Flow<List<Message>>

    @Query("DELETE FROM messages")
    suspend fun deleteAllChatMessages()

    @Query("DELETE FROM messages WHERE chat_id = :chatId")
    suspend fun deleteSingleChatMessages(chatId: String)

    @Query("DELETE FROM messages WHERE NOT chat_id = :chatId")
    suspend fun deleteOtherChatMessages(chatId: String)
}