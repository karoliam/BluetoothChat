package com.karoliinamultas.bluetoothchat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class BluetoothChatDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var Instance: BluetoothChatDatabase? = null

        fun getDatabase(context: Context): BluetoothChatDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    BluetoothChatDatabase::class.java,
                    "bluetooth_chat_database"
                ).build()
                    .also { Instance = it }
            }
        }
    }
}