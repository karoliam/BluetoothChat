package com.karoliinamultas.bluetoothchat.ui.chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.service.ChatForegroundService.Companion.MESSAGE_NOTIFICATION_ID

interface NotificationManagerWrapper {
    fun showNotification(title: String, message: String)
}
class NotificationManagerWrapperImpl(private val context: Context) : NotificationManagerWrapper {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun showNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channel =
                NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)

            val builder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.highlighter_size_4_40px)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(MESSAGE_NOTIFICATION_ID, builder.build())
        } else {
            val builder = NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.highlighter_size_4_40px)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            notificationManager.notify(MESSAGE_NOTIFICATION_ID, builder.build())
        }
    }
}


