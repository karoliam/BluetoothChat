package com.karoliinamultas.bluetoothchat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.karoliinamultas.bluetoothchat.R

class ChatForegroundService() : Service() {

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val MESSAGE_NOTIFICATION_ID = 2
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New message received")
                .setContentText("You have a new message")
                .setSmallIcon(R.drawable.highlighter_size_4_40px)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(MESSAGE_NOTIFICATION_ID.toInt(), builder.build())
        } else {
            val builder = NotificationCompat.Builder(this)
                .setContentTitle("New message received")
                .setContentText("You have a new message")
                .setSmallIcon(R.drawable.highlighter_size_4_40px)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(MESSAGE_NOTIFICATION_ID.toInt(), builder.build())
        }

        return START_STICKY
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}
