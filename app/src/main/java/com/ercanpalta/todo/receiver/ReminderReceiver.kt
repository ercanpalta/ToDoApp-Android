package com.ercanpalta.todo.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ercanpalta.todo.MainActivity
import com.ercanpalta.todo.R
import com.ercanpalta.todo.database.ToDoDatabase
import com.ercanpalta.todo.model.ToDo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date

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

        // to get task info
        var requestCode = 0
        p1?.extras?.apply {
            requestCode = this.getInt("request_code")
        }

        val dao = ToDoDatabase(p0.applicationContext).dao()
        var task:ToDo
        runBlocking {
            task = dao.getTaskByRequestCode(requestCode)
        }

        // to update reminder state
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = task.remindTimeInMillis
        if (task.repeat == "Does not repeat"){
            GlobalScope.launch {
                dao.updateRequestCode(requestCode)
            }
        }else if (task.repeat == "Daily"){
            calendar.add(Calendar.DATE, 1)
            task.remindTimeInMillis = calendar.timeInMillis
            GlobalScope.launch {
                dao.updateTask(task)
            }
        }else if (task.repeat == "Weekly"){
            calendar.add(Calendar.DATE, 7)
            task.remindTimeInMillis = calendar.timeInMillis
            GlobalScope.launch {
                dao.updateTask(task)
            }
        }



        // to open app when user clicked to the notification
        val intent = Intent(p0, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("uid",task.uid)
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(p0, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // to build notification
        val builder = NotificationCompat.Builder(p0,"com.ercanpalta.todo.receiver")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(task.task)
            .setContentText(task.description)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(task.description))


        notificationManager.notify(0, builder.build())

        // to open activity when reminder received
        val sharedPreferences = p0.getSharedPreferences("com.ercanpalta.todo",MODE_PRIVATE)
        val isAppOpen = sharedPreferences.getBoolean("isAppOpen",false)

        if(isAppOpen){
            p0.startActivity(intent)
        }

    }



}