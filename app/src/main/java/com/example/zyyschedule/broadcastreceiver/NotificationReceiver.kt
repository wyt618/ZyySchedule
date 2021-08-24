package com.example.zyyschedule.broadcastreceiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Schedule
import com.google.gson.Gson


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager: NotificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val gson = Gson()
        val strSchedule = intent?.getStringExtra("remindSchedule")
        val schedule: Schedule = gson.fromJson(strSchedule, Schedule::class.java)
        if (intent?.action?.equals("Notification_Receiver") == true) {
            val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "schedule_notification")
            notification.setAutoCancel(false)
            notification.setSmallIcon(R.drawable.notification_icon)
            notification.setContentTitle(schedule.title)
            notification.setContentText(schedule.detailed)
            notification.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            val remindDialogReceiver = Intent("com.example.zyyschedule.broadcastreceiver.RemindDialogReceiver")
            remindDialogReceiver.putExtra("remindSchedule", intent.getStringExtra("remindSchedule"))
            val toRemindDialog = PendingIntent.getBroadcast(context, intent.getIntExtra("PendingIntentCode", 0) + 10000, remindDialogReceiver, 0)
            notification.setContentIntent(toRemindDialog)
            schedule.id?.let {
                notificationManager.notify(it, notification.build())
            }
            context.sendBroadcast(remindDialogReceiver)
        }
    }
}
