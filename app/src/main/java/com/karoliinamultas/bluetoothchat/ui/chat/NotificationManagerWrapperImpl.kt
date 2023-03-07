package com.karoliinamultas.bluetoothchat.ui.chat

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import com.karoliinamultas.bluetoothchat.MainActivity
import com.karoliinamultas.bluetoothchat.R
import com.karoliinamultas.bluetoothchat.service.ChatForegroundService.Companion.MESSAGE_NOTIFICATION_ID

interface NotificationManagerWrapper {
    fun showNotification(title: String, message: String): Notification
}
class NotificationManagerWrapperImpl(private val context: Context) : NotificationManagerWrapper {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun showNotification(title: String, message: String): Notification {
        val channelId = "my_channel_id"
        val channel =
            NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val resultIntent = Intent(context, MainActivity::class.java)

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = Notification.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.highlighter_size_4_40px)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)

        notificationManager.notify(MESSAGE_NOTIFICATION_ID, builder.build())
        return builder.build()


    }
}


