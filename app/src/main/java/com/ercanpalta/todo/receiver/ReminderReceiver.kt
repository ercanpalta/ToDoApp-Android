package com.ercanpalta.todo.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ercanpalta.todo.MainActivity
import com.ercanpalta.todo.R

class ReminderReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        val notificationManager = p0?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // to create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("com.ercanpalta.todo.receiver", "Reminder",
                NotificationManager.IMPORTANCE_HIGH)
            channel.apply {
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // to get task info from add fragment
        var title = "title"
        var content = "content"
        p1?.extras?.apply {
            title = this.getString("title","title")
            content = this.getString("content","content")
        }

        // to open app when user clicked to the notification
        val intent = Intent(p0, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(p0, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // to build notification
        val builder = NotificationCompat.Builder(p0,"com.ercanpalta.todo.receiver")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(content))


        notificationManager.notify(0, builder.build())
    }
}